package app.datasets

import geotrellis.vector.MultiPolygonFeature
import geotrellis.vector.io.json.GeoJson
import geotrellis.vector.io.json.JsonFeatureCollection
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.DataFrame

final case class GeoDataset(
  datasetDir:     String
)(implicit spark: SparkSession)
    extends DatasetImplicits
    with DatasetBase
    with Pathfinder {
  import app.models._
  import app.models.json.proto._
  import spark.implicits._

  private val log: Logger = LogManager.getLogger("GeoDatasets")

  lazy val nyTaxiZones: Vector[MultiPolygonFeature[NyTaxiZonesGeoJsonData]] =
    GeoJson
      .parse[JsonFeatureCollection] {
        spark.read
          .option("wholetext", "true")
          .text(getDatasetAbsolutePathURI("ny_taxi_zones.geojson"))
          .collect()
          .map(_.getAs[String](0))
          .headOption
          .getOrElse("something wrong with geojson data")
      }
      .getAllMultiPolygonFeatures[NyTaxiZonesGeoJsonData]()

  lazy val nyTaxiZonesBroadcast
    : Broadcast[Vector[MultiPolygonFeature[NyTaxiZonesGeoJsonData]]] =
    spark.sparkContext.broadcast(nyTaxiZones)

  lazy val nyTaxiZonesLookup = {
    val fName = getDatasetAbsolutePathURI("taxi_zone_lookup.csv")
    readCsvA[NyTaxiZonesLookup](
      fName = fName
    )
  }

}
