package app.mods

import org.apache.spark.sql.SparkSession
import org.apache.log4j.LogManager
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import java.time.ZoneId
import java.time.ZoneOffset

object NYTDStatsMod {

  import app.datasets._
  import app.models.config.AppConfig

  private val log = LogManager.getLogger("NYTDStatsMod")

  def run(appConfig: AppConfig)(implicit spark: SparkSession): Unit = {

    log.debug("Running NYTDStatsMod")

    val nytDataSet: NYTDataSets =
      NYTDataSets(datasetDir = appConfig.files.datasetDir)(spark)

    import nytDataSet._
    println(s"Num partitions[${yellowTripDataDS_11_24.rdd.getNumPartitions}]")
    yellowTripDataDS_11_24.explain()
    yellowTripDataDS_11_24.printSchema()
    //  log.warn(s"yellowTripDataDS.count[${yellowTripDataDS.count()}]")

    yellowTripDataDS_11_24.show(100)

  }

}
