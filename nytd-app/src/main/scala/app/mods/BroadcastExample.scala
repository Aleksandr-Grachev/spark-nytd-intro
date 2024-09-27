package app.mods

import app.models.config.AppConfig
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object BroadcastExample {

  def run(appConfig: AppConfig)(implicit spark: SparkSession): Unit = {
    val sc:           SparkContext          = spark.sparkContext
    val broadcastVar: Broadcast[Array[Int]] = sc.broadcast(Array(1, 2, 3, 4, 5))
    val rdd:          RDD[Int]              = sc.parallelize(Array(2, 3, 4, 5, 6))
    val result:       RDD[Int]              = rdd.map(x => x * broadcastVar.value(1))
    println("=======")
    result.collect().foreach(println)
    println("=======")
    sc.stop()
  }

}
