package app.mods

import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.rdd.RDD
import app.models.YellowTripData

object NYTDStatsMod {

  import app.datasets._
  import app.models.config.AppConfig

  private val log = LogManager.getLogger("NYTDStatsMod")

  def run(appConfig: AppConfig)(implicit spark: SparkSession): Unit = {

    log.debug("Running NYTDStatsMod")

    //Geo
    val geoDataset = GeoDataset(
      datasetDir = appConfig.files.datasetDir
    )(spark)

    import geoDataset._

    // nyTaxiZonesLookup.explain()
    // nyTaxiZonesLookup.printSchema()
    // nyTaxiZonesLookup.show()

    //Yellow taxi
    val yellowTaxiDataset: YellowTaxiDataset =
      YellowTaxiDataset(
        datasetDir = appConfig.files.datasetDir
      )(geoDataset.nyTaxiZones)(spark)

    import yellowTaxiDataset._

    // println(s"Num partitions[${yellowTripDataDS_11_24.rdd.getNumPartitions}]")
    // yellowTripDataDS_11_24.explain()
    // yellowTripDataDS_11_24.printSchema()
    // yellowTripDataDS_11_24.show(100)

    // println(s"Num partitions[${yellowTripDataDS_10_09.rdd.getNumPartitions}]")
    // yellowTripDataDS_10_09.explain()
    // yellowTripDataDS_10_09.printSchema()
    // yellowTripDataDS_10_09.show(100)

    //zones.value.foreach(zone => println(zone.data))

    // println(
    //   s"Num partitions[${yellowTripDataDS_10_09_To_11_24.getNumPartitions}]"
    // )
    // val df: RDD[YellowTripData] = yellowTripDataDS_10_09_To_11_24
    // df.take(25).foreach(println)
    // yellowTripDataDS_10_09_To_11_24.toDF().explain()
    // yellowTripDataDS_10_09_To_11_24.printSchema()
    // yellowTripDataDS_10_09_To_11_24.show(1000)

    val greenTripDataset: GreenTaxiDataset =
      GreenTaxiDataset(datasetDir = appConfig.files.datasetDir)(spark)

    import greenTripDataset._

    println(s"Num partitions[${greenTripData.rdd.getNumPartitions}]")

    greenTripData.toDF().explain()
    greenTripData.printSchema()
    greenTripData.show(100)

  }

}
