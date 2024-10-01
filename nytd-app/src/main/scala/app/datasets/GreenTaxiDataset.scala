package app.datasets

import cats.kernel.instances.double._
import cats.kernel.instances.int._
import cats.kernel.instances.long._
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.execution.columnar.LONG

final case class GreenTaxiDataset(
  datasetDir: String
)(
  spark: SparkSession
) extends DatasetImplicits
    with Pathfinder {
  import app.models._
  import spark.implicits._

  val greeTDforYearA = forYearA("green_tripdata") _

  val greenTDforYearB = forYearB("green_tripdata") _

  lazy val greenTripData = {

    val paths =
      (greeTDforYearA("2024").collect {
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
          "2014"
        )
          .flatMap(greenTDforYearB))
        .map(getDatasetAbsolutePathURI)

    paths.par
      .map { path =>
        val frame: DataFrame = spark.read.parquet(path)

        frame
          .withColumnCast[Int]("RatecodeID", "int")
          .withColumnCast[Int]("passenger_count", "int")
          .withColumnCast[Long]("payment_type", "long")
          .withColumnCast[Int]("trip_type", "int")

      }
      .reduce(_ union _)
      .as[GreenTripData]
      .filter(
        col("lpep_pickup_datetime") < unix_timestamp(
          lit("2014-01-02 00:00:00")
        ).cast("timestamp")
      )

  }

}
