package app.datasets

import geotrellis.vector.io.json.GeoJson
import geotrellis.vector.io.json.JsonFeatureCollection
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.apache.spark.sql.SparkSession
import geotrellis.vector.MultiPolygonFeature
import cats.kernel.instances.double._
import cats.kernel.instances.int._
import cats.kernel.instances.long._

final case class GeoDatasets(
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
        getDatasetAbsolutePath("ny_taxi_zones.geojson")
      )
      .getAllMultiPolygonFeatures[NyTaxiZonesGeoJsonData]()

  lazy val nyTaxiZonesLookup = {
    val fName = getDatasetAbsolutePathURI("taxi_zone_lookup.csv")
    readCsvA[NyTaxiZonesLookup](
      fName = fName
    )
  }

}
