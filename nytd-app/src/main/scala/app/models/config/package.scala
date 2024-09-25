package app.models

import java.net.URI
import pureconfig.ConfigReader
import pureconfig.ConvertHelpers
import pureconfig.ConfigWriter

package object config {

  object AppModulesEnum extends Enumeration {
    type AppModulesType = Value

    val Main, Samples = Value
  }

  case class AppConfig(
    name:      String,
    runModule: AppModulesEnum.AppModulesType,
    files:     FilesConfig
  )

  case class FilesConfig(datasetDir: String, s3: Option[S3Config])

  case class S3Config(endpoint: String, accessKey: String, secretKey: String)

  implicit val appModuleConfigReader: ConfigReader[AppModulesEnum.AppModulesType] =
    ConfigReader.fromString[AppModulesEnum.AppModulesType](
      ConvertHelpers.catchReadError(str => AppModulesEnum.withName(s = str))
    )

  implicit val appModuleConfigWriter
    : ConfigWriter[AppModulesEnum.AppModulesType] =
    ConfigWriter[String].contramap[AppModulesEnum.AppModulesType](_.toString())

}
