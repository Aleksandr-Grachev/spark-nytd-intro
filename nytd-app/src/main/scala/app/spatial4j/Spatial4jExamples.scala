package app.spatial4j

import org.locationtech.spatial4j.context._
import org.locationtech.spatial4j.shape.Shape
import org.locationtech.spatial4j.io.GeoJSONReader
import org.locationtech.spatial4j.io.ShapeIO
import org.locationtech.spatial4j.context.jts.JtsSpatialContext

object Spatial4jExamples extends App {

  val ctx = JtsSpatialContext.GEO

  val readerWKT = ctx.getFormats().getWktReader()

  val gjr = ctx.getFormats().getReader(ShapeIO.GeoJSON);

  assert(gjr != null, "The gjt is null")

  val geoJSONReader: GeoJSONReader =
    new GeoJSONReader(ctx, new SpatialContextFactory());

  assert(geoJSONReader != null, "The Geo Json reader is null")

  println(geoJSONReader.getClass().getName())

  println(readerWKT.getClass().getName())

  assert(readerWKT != null, "The WKT reader is null")

  val sh: Shape =
    geoJSONReader.read(s"""
            |{
            |   "type": "Feature",
            |   "geometry": {
            |    "type": "Point",
            |    "coordinates": [102.0, 0.5]
            |    },
            |   "properties": {
            |    "name": "Sample Point",
            |    "population": 200
            |   }
            |}
    """.stripMargin)

  println(sh.getBoundingBox().getMaxY())
  println(sh.getBoundingBox().getMinX())

  val source =
    scala.io.Source
      .fromFile("app-vol/datasets/nytd/222.geojson")
      .mkString

  val taxiZones = gjr.read(source)
  println(taxiZones.getBoundingBox().getMaxY())
  println(taxiZones.getBoundingBox().getMinX())

  val source2 =
    scala.io.Source
      .fromFile("app-vol/datasets/nytd/ny_taxi_zones.geojson")
      .mkString

  val taxiZones2 = gjr.read(source2)
  println(taxiZones2.getBoundingBox().getMaxY())
  println(taxiZones2.getBoundingBox().getMinX())

}
