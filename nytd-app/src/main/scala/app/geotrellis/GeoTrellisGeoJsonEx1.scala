package app.geotrellis

import geotrellis.vector.Point
import geotrellis.vector.io.json.GeoJson
import geotrellis.vector.io.json.GeoJsonSupport._
import geotrellis.vector.io.json.Implicits._
import geotrellis.vector.io.json.JsonFeatureCollection
import geotrellis.vector.MultiPolygonFeature

object GeoTrellisGeoJsonEx1 extends App {

  import io.circe._
  import io.circe.generic.semiauto._

  val pointJson = Point(1, 1).toGeoJson
  println(pointJson)
  assert(
    pointJson == """{"type":"Point","coordinates":[1.0,1.0]}"""
  )

  val featureCollection: JsonFeatureCollection =
    GeoJson.parse[JsonFeatureCollection](
      scala.io.Source.fromResource("mp-example.geojson").mkString
    )

  assert(featureCollection != null)

  case class ExampleMultipolygonFeature(
    example_prop_1: Int,
    another_prop_3: Double
  )

  implicit val decoder: Decoder[ExampleMultipolygonFeature] =
    deriveDecoder[ExampleMultipolygonFeature]

  val allMPF: Vector[MultiPolygonFeature[ExampleMultipolygonFeature]] =
    featureCollection.getAllMultiPolygonFeatures[ExampleMultipolygonFeature]()

  allMPF.foreach { mpf =>
    println(mpf.data)
    println(mpf.geom.toText()) //WKT repr
  }

}
