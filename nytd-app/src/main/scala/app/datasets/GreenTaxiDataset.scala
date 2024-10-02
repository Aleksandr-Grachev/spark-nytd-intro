package app.datasets

import org.apache.spark.sql._

final case class GreenTaxiDataset(
  datasetDir: String
)(
  spark: SparkSession
) extends DatasetImplicits
    with Pathfinder {
  import app.models._
  import spark.implicits._

  val greeTDforYearA: String => IndexedSeq[(Int, String)] = forYearA(
    "green_tripdata"
  ) _

  val greenTDforYearB: String => IndexedSeq[String] = forYearB(
    "green_tripdata"
  ) _

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
          .withColumnOptionCast("RatecodeID", "int")
          .withColumnOptionCast("passenger_count", "int")
          .withColumnOptionCast("payment_type", "long")
          .withColumnOptionCast("trip_type", "int")

      }
      .reduce(_ union _)
      .as[GreenTripData]
    // .filter( //FIXME:remove after check models
    //   col("lpep_pickup_datetime") < unix_timestamp(
    //     lit("2014-01-02 00:00:00")
    //   ).cast("timestamp")
    // )

  }

}
