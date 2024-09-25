package ru.neoflex.imdbApp.app.stats

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import pureconfig._
import pureconfig.error.ConfigReaderFailures
import pureconfig.error.ThrowableFailure
import pureconfig.generic.auto._

class AppConfigTest extends AnyWordSpec {

  import app.models.config._

  "AppConfig" when {

    "load" should {

      "return config value correctly" in {
        val cfgStr: String =
          s"""
           "app" : {
             "files" : {
              "dataset-dir" : "s3a://my-bucket/ds",
              "s-3" : {
                "access-key" : "test-access-key",
                "endpoint" : "https://example.com",
                "secret-key" : "secret_key_test"
               }
            },
            "name" : "nytd-app",
            "run-module" : "Main"
            }
          """

        val actual = ConfigSource.string(cfgStr).at("app").load[AppConfig]

        println(actual)

      }

    }
  }
}
