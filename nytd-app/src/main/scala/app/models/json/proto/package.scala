package app.models.json

package object proto {
  import app.models._

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val nyTaxiZonesGeoJsonData: Decoder[NyTaxiZonesGeoJsonData] =
    deriveDecoder[NyTaxiZonesGeoJsonData]

}
