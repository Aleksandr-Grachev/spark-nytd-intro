package app.datasets

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

final case class FHVTaxiDataset(
  datasetDir: String
)(
  spark: SparkSession
) extends DatasetImplicits
    with Pathfinder {
  import app.models._
  import spark.implicits._

  val fhvTDforYearA = forYearA("fhv_tripdata") _

  val fhvTDforYearB = forYearB("fhv_tripdata") _

  lazy val fhvTripData = {

    val paths =
      (fhvTDforYearA("2024").collect {
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
          "2015"
        )
          .flatMap(fhvTDforYearB))
        .map(getDatasetAbsolutePathURI)

    paths.par
      .map { path =>
        val frame: DataFrame = spark.read.parquet(path)

        frame
          .withColumnOptionCast("PUlocationID", "long")
          .withColumnOptionCast("DOlocationID", "long")
          .withColumnOptionCast("SR_Flag", "int")

      }
      .reduce(_ union _)
      .as[FHVTripData]
      .filter( //FIXME: remove after check the model
        col("pickup_datetime") < unix_timestamp(
          lit("2015-01-02 00:00:00")
        ).cast("timestamp")
      )

  }

}
