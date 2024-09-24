package app.datasets

import org.apache.spark.sql.Encoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._

trait DatasetBase {

  def readCsvA[R: Encoder](
    fName:          String
  )(implicit spark: SparkSession): Dataset[R] =
    readDataset[R](
      format = "csv",
      options = Map(
        "header"    -> "true",
        "delimiter" -> ","
      ),
      fName = fName
    )
      .as[R]

  def readCsvB[R: Encoder, T: Encoder](
    fName: String
  )(f:     R => T)(implicit spark: SparkSession): Dataset[T] =
    readDataset[R](
      format = "csv",
      options = Map(
        "header"    -> "true",
        "delimiter" -> ","
      ),
      fName = fName
    )
      .as[R]
      .map(f)

  def readDataset[R: Encoder](
    format:         String,
    options:        Map[String, String],
    fName:          String
  )(implicit spark: SparkSession) =
    options
      .foldLeft(spark.read.format(format)) { case (reader, (option, value)) =>
        reader.option(option, value)
      }
      .schema(schema = implicitly[Encoder[R]].schema)
      .load(fName)

}
