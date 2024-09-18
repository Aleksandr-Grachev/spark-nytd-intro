package app.datasets

import cats.kernel.Monoid
import cats.kernel.instances.double._
import cats.kernel.instances.int._
import cats.kernel.instances.long._
import org.apache.log4j.LogManager
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

final case class NYTDataSets(
  datasetDir: String
)(spark:      SparkSession) {
  import app.models._
  import spark.implicits._

  import NYTDataSets._

  private val log = LogManager.getLogger("NYTDataSets")

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
        .map(getDatasetAbsolutePath)

    paths.foreach(log.warn _)

    paths.par
      .map { path =>
        val frame: DataFrame = spark.read.parquet(path)

        frame
          .withColumnOrEmpty[Long]("Rec")
          .withColumnCast[Int]("passenger_count", "int")
          // .withColumnCast[Long]("Rec", "long") TODO: ???
          .withColumnCast[Double]("improvement_surcharge", "double")
          .withColumnCast[Int]("RatecodeID", "int")

      }
      .reduce(_ union _)
      .as[YellowTripData]
    // .filter(
    //   col("tpep_dropoff_datetime") < unix_timestamp(
    //     lit("2011-01-01 00:00:00")
    //   ).cast("timestamp")
    // )

  }

  lazy val yellowTripDataDS_10_ = {

    val paths =
      Seq(
        "2010",
        "2009"
      )
        .flatMap(yellowTDforYearB)
        .map(getDatasetAbsolutePath)

    paths.foreach(log.warn _)

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
              "Fare_Amt"              -> "fare_amount",
              "Tip_Amt"               -> "tip_amount",
              "Tolls_Amt"             -> "tolls_amount",
              "Total_Amt"             -> "total_amount"
            )
          )
          .withColumnCast[Int]("vendor_id", "int")
          .withColumnCast[Long]("rate_code", "long")
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
      .reduce(_ union _)
      .as[YellowTripData_10_09]
      .filter(
        col("pickup_datetime") < unix_timestamp(
          lit("2009-02-01 00:00:00")
        ).cast("timestamp")
      )
  }

  def getDatasetAbsolutePath(pFileName: String) =
    s"${datasetDir}/$pFileName"

}

object NYTDataSets {

  implicit class DataFrameOps(frame: DataFrame) {

    def withColumnOrEmpty[T: Monoid](
      colName: String
    ): DataFrame = {
      val existentCols = frame.columns
      if (existentCols.contains(colName)) {
        frame
      } else {
        frame.withColumn(colName, lit(implicitly[Monoid[T]].empty))
      }
    }

    def withColumnCast[Target: Monoid](
      colName: String,
      castTo:  String
    ): DataFrame =
      frame
        .withColumn(
          colName,
          when(col(colName).isNull, lit(implicitly[Monoid[Target]].empty))
            .otherwise(col(colName).cast(castTo))
        )

    def withColumnOptionCast(
      colName: String,
      castTo:  String
    ): DataFrame =
      frame
        .withColumn(
          colName,
          when(col(colName).isNotNull, col(colName).cast(castTo))
        )

  }

  def readDataFrame[R: Encoder](
    paths:          Seq[String]
  )(implicit spark: SparkSession): DataFrame =
    spark.read
      .schema(schema = implicitly[Encoder[R]].schema)
      .option("mergeSchema", "true")
      .parquet(paths: _*)

  def readDataset[R: Encoder, T: Encoder](
    paths: Seq[String]
  )(f:     R => T)(implicit spark: SparkSession): Dataset[T] =
    spark.read
      .schema(schema = implicitly[Encoder[R]].schema)
      .parquet(paths: _*)
      .as[R]
      .map(f)

  def forYearA(dsName: String)(yyyy: String): IndexedSeq[(Int, String)] =
    12 to 1 by -1 map { i =>
      i -> f"${dsName}_${yyyy}-$i%02d.parquet"
    }

  def forYearB(dsName: String)(yyyy: String): IndexedSeq[String] =
    forYearA(dsName)(yyyy).map(_._2)

}
