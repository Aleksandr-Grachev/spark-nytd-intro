import sbt._
import sbt.librarymanagement.DependencyBuilders

object Dependency {

  lazy val scalaTestLibs = {
    lazy val scalatestVersion = "3.2.19"
    Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion % Test
    )
  }

  lazy val loggingLibs = {
    val jacksonV = "2.15.2"
    val log4jV   = "2.20.0"

    Seq(
      "org.apache.logging.log4j" % "log4j-api"  % log4jV,
      "org.apache.logging.log4j" % "log4j-core" % log4jV
    )
  }

  lazy val pureConfigLibs = {
    val pureConfigV = "0.17.6"
    Seq(
      "com.github.pureconfig" %% "pureconfig" % "0.17.6"
    )
  }

  lazy val scoptLibs: Seq[ModuleID] = {
    val V = "4.1.0"
    Seq(
      "com.github.scopt" %% "scopt" % V
    )
  }

  object catsLibs {
    val coreV = "2.12.0"

    lazy val catsCoreDeps =
      Seq(
        "org.typelevel" %% "cats-core" % coreV
      )
  }

  object GeoTrellisLibs {
    val V = "3.7.1"

    //see https://geotrellis.readthedocs.io/en/latest/guide/module-hierarchy.html
    lazy val deps = Seq(
      "org.locationtech.geotrellis" %% "geotrellis-vector" % V
    )
  }

  object sparkLibs {
    val sparkV = "3.5.2"
    //val sparkTestingV = s"${sparkV}_1.5.3"
    val sparkTestingV = s"3.5.1_1.5.3" //TODO: check for updates

    lazy val sparkCoreDeps: Seq[ModuleID] =
      Seq(
        "org.apache.spark" %% "spark-core" % sparkV,
        "org.apache.spark" %% "spark-sql"  % sparkV
      )

    lazy val sparkTestLibs: Seq[ModuleID] =
      Seq(
        "com.holdenkarau" %% "spark-testing-base" % sparkTestingV % Test
      )
  }

}
