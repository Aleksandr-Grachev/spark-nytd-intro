package app.mods

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._

object NYTDStatsMod {

  import app.datasets._
  import app.models.config.AppConfig

  private val log = LogManager.getLogger("NYTDStatsMod")

  def run(appConfig: AppConfig)(implicit spark: SparkSession): Unit = {

    log.debug("Running NYTDStatsMod")

    val nytDataSet: NYTDataSets =
      NYTDataSets(datasetDir = appConfig.files.datasetDir)(spark)

    import nytDataSet._
    // println(s"Num partitions[${yellowTripDataDS_11_24.rdd.getNumPartitions}]")
    // yellowTripDataDS_11_24.explain()
    // yellowTripDataDS_11_24.printSchema()
    // yellowTripDataDS_11_24.show(100)

    println(s"Num partitions[${yellowTripDataDS_10_.rdd.getNumPartitions}]")
    yellowTripDataDS_10_.explain()
    yellowTripDataDS_10_.printSchema()
    yellowTripDataDS_10_.show(100)

  }

}
