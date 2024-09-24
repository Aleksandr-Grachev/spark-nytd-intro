package app.datasets

import cats.kernel.Monoid
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

trait DatasetImplicits {

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

}
