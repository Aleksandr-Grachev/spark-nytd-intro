package app.datasets

import app.models.NyTaxiZonesGeoJsonData
import cats.kernel.instances.double._
import cats.kernel.instances.int._
import cats.kernel.instances.long._
import geotrellis.vector.MultiPolygonFeature
import geotrellis.vector.Point
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

final case class YellowTaxiDataset(
  datasetDir:  String
)(nyTaxiZones: => Vector[MultiPolygonFeature[NyTaxiZonesGeoJsonData]])(
  spark:       SparkSession
) extends DatasetImplicits
    with Pathfinder {
  import app.models._
  import spark.implicits._

  val yellowTDforYearA = forYearA("yellow_tripdata") _

  val yellowTDforYearB = forYearB("yellow_tripdata") _

  lazy val yellowTripDataDS_11_24 = {

    val paths =
      (yellowTDforYearA("2024").collect {
        case (m, p) if m < 7 => p
      } ++
        Seq(
          "2023",
          "2022",
          "2021",
          "2020",
          "2019",
          "2018",
          "2017",
          "2016",
          "2015",
          "2014",
          "2013",
          "2012",
          "2011"
        )
          .flatMap(yellowTDforYearB))
        .map(getDatasetAbsolutePathURI)

    paths.par
      .map { path =>
        val frame: DataFrame = spark.read.parquet(path)

        frame
          .drop("Rec")
          .withColumnCast[Double]("Airport_fee", "double")
          .withColumnCast[Int]("passenger_count", "int")
          // .withColumnCast[Long]("Rec", "long") TODO: ???
          .withColumnCast[Double]("improvement_surcharge", "double")
          .withColumnCast[Double]("congestion_surcharge", "double")
          .withColumnCast[Int]("RatecodeID", "int")

      }
      .reduce(_ unionByName _)
      .as[YellowTripData]
    // .filter(
    //   col("tpep_dropoff_datetime") < unix_timestamp(
    //     lit("2011-01-01 00:00:00")
    //   ).cast("timestamp")
    // )

  }

  lazy val yellowTripDataDS_10_09: Dataset[YellowTripData_10_09] = {

    val paths =
      Seq(
        "2010",
        "2009"
      )
        .flatMap(yellowTDforYearB)
        .map(getDatasetAbsolutePathURI)

    paths.par
      .map { path =>
        val frame: DataFrame = spark.read.parquet(path)
        frame
          .drop("__index_level_0__") //TODO: 2010-03|02 have this unknown column
          .withColumnsRenamed(
            Map(
              "vendor_name"           -> "vendor_id",
              "Trip_Pickup_DateTime"  -> "pickup_datetime",
              "Trip_Dropoff_DateTime" -> "dropoff_datetime",
              "Start_Lon"             -> "pickup_longitude",
              "Start_Lat"             -> "pickup_latitude",
              "End_Lon"               -> "dropoff_longitude",
              "End_Lat"               -> "dropoff_latitude",
              "Payment_Type"          -> "payment_type",
              "Rate_Code"             -> "rate_code",
              "store_and_forward"     -> "store_and_fwd_flag",
              "Fare_Amt"              -> "fare_amount",
              "Tip_Amt"               -> "tip_amount",
              "Tolls_Amt"             -> "tolls_amount",
              "Total_Amt"             -> "total_amount"
            )
          )
          .withColumnCast[Double]("dropoff_longitude", "double")
          .withColumnCast[Double]("dropoff_latitude", "double")
          .withColumnCast[Int]("vendor_id", "int")
          .withColumnCast[Long]("rate_code", "long")
          .withColumnCast[Int]("passenger_count", "int")
          .withColumnOptionCast(
            "pickup_datetime",
            "timestamp"
          )
          .withColumnOptionCast(
            "dropoff_datetime",
            "timestamp"
          )
          .withColumn(
            "payment_type",
            when(
              col("payment_type").isNull or col("payment_type") === "N",
              5 /* = Unknown */ //TODO: move payment_type's to enum
            ).cast("long")
          )
      }
      .reduce(_ unionByName _)
      .as[YellowTripData_10_09]

  }

  val zones: Broadcast[Vector[MultiPolygonFeature[NyTaxiZonesGeoJsonData]]] =
    spark.sparkContext.broadcast(nyTaxiZones)

  lazy val yellowTripDataDS_10_09_To_11_24 =
    yellowTripDataDS_10_09.rdd.map {
      case YellowTripData_10_09(
            vendor_id,
            pickup_datetime,
            dropoff_datetime,
            passenger_count,
            trip_distance,
            pickup_longitude,
            pickup_latitude,
            rate_code,
            store_and_fwd_flag,
            dropoff_longitude,
            dropoff_latitude,
            payment_type,
            fare_amount,
            surcharge,
            mta_tax,
            tip_amount,
            tolls_amount,
            total_amount
          ) =>
        val doLocationID: Int =
          zones.value
            .find { mPFeature =>
              mPFeature.geom.contains(
                Point(dropoff_longitude, dropoff_latitude)
              )
            }
            .map {
              _.data.location_id
            }
            .getOrElse(265) //see ny_taxi_zones_lookup, N/A location

        val puLocationID: Int =
          zones.value
            .find { mPFeature =>
              mPFeature.geom.contains(
                Point(pickup_longitude, pickup_latitude)
              )
            }
            .map {
              _.data.location_id
            }
            .getOrElse(264) //see ny_taxi_zones_lookup, Unknown location

        YellowTripData(
          Airport_fee = 0.0d, //they set this field to 0.0 in old data
          congestion_surcharge = surcharge,
          DOLocationID = doLocationID.toLong,
          extra =
            0.0, //Currently, this only includes the $0.50 and $1 rush hour and overnight charges
          fare_amount = fare_amount,
          improvement_surcharge =
            0.0, //the improvement surcharge began being levied in 2015
          mta_tax = mta_tax,
          passenger_count = passenger_count,
          payment_type = payment_type,
          PULocationID = puLocationID.toLong,
          RatecodeID = rate_code, //todo:check this
          store_and_fwd_flag = store_and_fwd_flag,
          tip_amount = tip_amount,
          tolls_amount = tolls_amount,
          total_amount = total_amount,
          tpep_dropoff_datetime = dropoff_datetime,
          tpep_pickup_datetime = pickup_datetime,
          trip_distance = trip_distance,
          VendorID = vendor_id
        )
    }

  lazy val yellowTripDataTotalDS: Dataset[YellowTripData] =
    yellowTripDataDS_11_24.unionByName(
      yellowTripDataDS_10_09_To_11_24.toDS()
    )

}
