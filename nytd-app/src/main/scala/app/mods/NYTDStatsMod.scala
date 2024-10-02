package app.mods

import app.stats.HeatMapper
import org.apache.log4j.LogManager
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._

object NYTDStatsMod {

  import app.datasets._
  import app.models.config.AppConfig

  private val log = LogManager.getLogger("NYTDStatsMod")

  def run(appConfig: AppConfig)(implicit spark: SparkSession): Unit = {

    log.debug("Running NYTDStatsMod")

    import spark.implicits._

    //Geo
    val geoDataset = GeoDataset(
      datasetDir = appConfig.files.datasetDir
    )(spark)

    import geoDataset._

    // nyTaxiZonesLookup.explain()
    // nyTaxiZonesLookup.printSchema()
    // nyTaxiZonesLookup.show()

    //Yellow taxi trip data
    val yellowTaxiDataset: YellowTaxiDataset =
      YellowTaxiDataset(
        datasetDir = appConfig.files.datasetDir
      )(geoDataset.nyTaxiZones)(spark)

    import yellowTaxiDataset._

    //  println(s"Num partitions[${yellowTripDataDS_11_24.rdd.getNumPartitions}]")
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
    // yellowTripDataDS_10_09_To_11_24.toDS().explain()
    // yellowTripDataDS_10_09_To_11_24.toDS().printSchema()
    // yellowTripDataDS_10_09_To_11_24.toDS.show(100)

    //yellowTripDataTotalDS.printSchema()

    //Green taxi trip data
    val greenTripDataset: GreenTaxiDataset =
      GreenTaxiDataset(datasetDir = appConfig.files.datasetDir)(spark)

    import greenTripDataset._

    // println(s"Num partitions[${greenTripData.rdd.getNumPartitions}]")

    // greenTripData.toDF().explain()
    // greenTripData.printSchema()
    // greenTripData.show(100)

    //FHV trip data
    val fhvDataset =
      FHVTaxiDataset(datasetDir = appConfig.files.datasetDir)(spark)
    // println(s"Num partitions[${fhvTripData.rdd.getNumPartitions}]")

    // fhvTripData.toDF().explain()
    // fhvTripData.printSchema()
    // fhvTripData.show(100)

    val heatMapper = new HeatMapper(
      greenTripDataset = greenTripDataset.greenTripData,
      yellowTripDataset = yellowTaxiDataset.yellowTripDataTotalDS,
      fhvTripDataset = fhvDataset.fhvTripData
    )(spark)

    import heatMapper._

    println(s"Num partitions[${combinedPOLoc.rdd.getNumPartitions}]")
    combinedPOLoc.explain()
    combinedPOLoc.printSchema()
    combinedPOLoc.show(100)
  }

}
