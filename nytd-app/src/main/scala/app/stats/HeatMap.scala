package app.stats

import org.apache.spark.sql.Dataset
import app.models._
import org.apache.spark.sql.SparkSession

class HeatMap(
  greenTripDataset:  Dataset[GreenTripData],
  yellowTripDataset: Dataset[YellowTripData],
  fhvTripDataset:    Dataset[FHVTripData]
)(implicit spark:    SparkSession) {

  import spark.implicits._

  lazy val combinedPOLoc =
    greenTripDataset
      .filter(_.passenger_count.nonEmpty)
      .map { greenTripData =>
        (
          greenTripData.PULocationID,
          greenTripData.passenger_count.getOrElse(0),
          greenTripData.lpep_pickup_datetime
        )
      }
      .union(
        yellowTripDataset.flatMap { yellowTripData =>
          yellowTripData.tpep_pickup_datetime.map { dt =>
            (
              yellowTripData.PULocationID,
              yellowTripData.passenger_count,
              dt
            )
          }
        }
      )
      .union(
        fhvTripDataset.flatMap { fhvTripData =>
          fhvTripData.PUlocationID.map { puLocID =>
            (puLocID, 1, fhvTripData.pickup_datetime) //предположение что если FHV доехало был как минимум 1 пассажир
          }
        }
      )
}
