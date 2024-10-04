package app.stats

import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

import app.models._
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalUnit

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

  /** @param unit, ChronoUnit. WARN: this method supports units up to DAYS
    * @return pair of zoneIs, passenger_count, timestamp truncated to unit
    */
  def passangersByZonesAnd(unit: TemporalUnit) =
    combinedPOLoc.map { case (zoneId, passengerCount, pickupDatetime) =>
      PassengerCountByZoneAndTime(
        zoneId = zoneId,
        pickupTS = pickupDatetime
          .truncatedTo(unit),
        passengerCount = passengerCount
      )
    }

  def heatMapData(unit: TemporalUnit) =
    passangersByZonesAnd(unit)
      .groupBy(col("zoneId"), col("pickupTS"))
      .agg(
        Map(
          "passengerCount" -> "sum"
        )
      )
}
