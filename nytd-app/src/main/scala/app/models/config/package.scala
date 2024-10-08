package app.models

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

  case class FilesConfig(
    datasetDir: String
  )

}
