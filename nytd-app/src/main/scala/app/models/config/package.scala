package app.models

import pureconfig.ConfigReader
import pureconfig.ConfigWriter
import pureconfig.ConvertHelpers

package object config {

  object AppModulesEnum extends Enumeration {
    type AppModulesType = Value

    val Main, Samples,BroadcastExample = Value
  }

  case class Passport(value: String) {
    override def toString: String = "*****"
  }

  case class AppConfig(
    name:      String,
    runModule: AppModulesEnum.AppModulesType,
    files:     FilesConfig
  )

  case class FilesConfig(datasetDir: String, s3: Option[S3Config])

  case class S3Config(
    endpoint:  String,
    accessKey: Passport,
    secretKey: Passport
  )

  implicit val passportConfigReader: ConfigReader[Passport] =
    ConfigReader.fromString[Passport](
      ConvertHelpers.catchReadError(str => Passport(str))
    )

  implicit val passportConfigWriter: ConfigWriter[Passport] =
    ConfigWriter[String].contramap[Passport](_ => "*****")

  implicit val appModuleConfigReader
    : ConfigReader[AppModulesEnum.AppModulesType] =
    ConfigReader.fromString[AppModulesEnum.AppModulesType](
      ConvertHelpers.catchReadError(str => AppModulesEnum.withName(s = str))
    )

  implicit val appModuleConfigWriter
    : ConfigWriter[AppModulesEnum.AppModulesType] =
    ConfigWriter[String].contramap[AppModulesEnum.AppModulesType](_.toString())

}
