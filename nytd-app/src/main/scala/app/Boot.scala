package app

import com.typesafe.config._
import org.apache.logging.log4j.LogManager
import org.apache.spark.SparkConf
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.error.ThrowableFailure
import pureconfig.generic.auto._

import scala.util.Properties
import app.session.SparkSessionCreator
import app.mods.NYTDStatsMod
import org.apache.spark.sql.SparkSession

object Boot extends SparkSessionCreator {
  import app.models.config._
  import app.BuildInfo
  import app.cli._

  val log = LogManager.getLogger(Boot.getClass())

  def main(args: Array[String]): Unit = {
    logBootHeader(args)

    (for {

      rawCfg <- ConfigSource.default.config()
      _ = logConfig(rawCfg, "The loaded config")

      cfgFromLoader <- ConfigSource.fromConfig(rawCfg).at("app").load[AppConfig]

      mergedWithCommandLineAppCfg <-
        loadAppConfig(args = args, init = cfgFromLoader).left.map { ex =>
          ConfigReaderFailures(ThrowableFailure(ex, None))
        }

      _ = logConfig(
        ConfigWriter[AppConfig].to(mergedWithCommandLineAppCfg).atPath("app"),
        "The harvested app config"
      )

    } yield {

      mergedWithCommandLineAppCfg.runModule match {

        case AppModulesEnum.Main =>
          val sparkConf: SparkConf =
            withS3(
              initial = withCaseInsensitive(
                withAppName(
                  new SparkConf,
                  mergedWithCommandLineAppCfg.name
                )
              ),
              endpointAccessKeyAndSecret =
                mergedWithCommandLineAppCfg.files.s3.map {
                  case S3Config(
                        endpoint,
                        accessKey,
                        secretKey
                      ) =>
                    (endpoint, accessKey, secretKey)
                }
            )

          implicit val spark: SparkSession = buildSparkSession(sparkConf)

          NYTDStatsMod.run(mergedWithCommandLineAppCfg)

        case AppModulesEnum.Samples =>
          val sparkConf: SparkConf =
            withPerExecutorMemory(
              withKryo(
                withAppName(
                  new SparkConf(),
                  "NYT   dataset samples generation"
                )
              ),
              "2G"
            )

          implicit val spark: SparkSession = buildSparkSession(sparkConf)

          ??? //TODO:impl

      }

      log.debug(
        "{} application completed",
        mergedWithCommandLineAppCfg.runModule
      )

    }).left
      .foreach { ex =>
        log.error(
          "An error[{}] occurred while starting application, exit",
          ex.prettyPrint()
        )
        System.exit(-1)
      }

  }

  def logBootHeader(args: Array[String]): Unit = {
    log.info(s"starting. {}", BuildInfo)
    log.debug(s"The application is launched with args[${args.mkString(" ")}]")
    log.debug(
      s"environment: javaVersion - ${Properties.javaVersion}, javaVendor - ${Properties.javaVendor}, " +
        s"javaVmInfo = ${Properties.javaVmInfo}, javaVmVendor - ${Properties.javaVmVendor}, " +
        s"javaVmVersion - ${Properties.javaVmVersion}"
    )
    for ((key, value) <- sys.env) {
      log.debug(s"env[$key = $value]")
    }

  }

  def logConfig(config: Config, info: String): Unit = {
    val renderOptions: ConfigRenderOptions =
      ConfigRenderOptions
        .defaults()
        .setOriginComments(false)
        .setComments(false)
        .setFormatted(true)

    log.debug(
      s"$info[{}]",
      config.root().render(renderOptions)
    )
  }

}
