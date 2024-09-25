package app.datasets

import geotrellis.vector.MultiPolygonFeature
import geotrellis.vector.io.json.GeoJson
import geotrellis.vector.io.json.JsonFeatureCollection
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession

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
      .fromFile[JsonFeatureCollection](
        getDatasetAbsolutePathURI("ny_taxi_zones.geojson")
      )
      .getAllMultiPolygonFeatures[NyTaxiZonesGeoJsonData]()

  lazy val nyTaxiZonesLookup = {
    val fName = getDatasetAbsolutePathURI("taxi_zone_lookup.csv")
    readCsvA[NyTaxiZonesLookup](
      fName = fName
    )
  }

}
