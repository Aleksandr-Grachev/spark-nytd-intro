package app.stats

import org.apache.spark.sql.Dataset
import app.models._
import org.apache.spark.sql.SparkSession
import java.time.Instant

class HeatMapper(
  greenTripDataset:  => Dataset[GreenTripData],
  yellowTripDataset: => Dataset[YellowTripData],
  fhvTripDataset:    => Dataset[FHVTripData]
)(implicit spark:    SparkSession) {

  import spark.implicits._

  lazy val combinedPOLoc: Dataset[(Long, Int, Instant)] =
    greenTripDataset
      .flatMap { greenTripData =>
        greenTripData.passenger_count.map { pc =>
          (
            greenTripData.PULocationID,
            pc,
            greenTripData.lpep_pickup_datetime
          )
        }
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
            (
              puLocID,
              1, //предположение, что, если FHV доехало, был как минимум 1 пассажир
              fhvTripData.pickup_datetime
            )
          }
        }
      )
}
