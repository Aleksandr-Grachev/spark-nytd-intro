package app.session

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import app.kryo.KryoReg

trait SparkSessionCreator {

  def buildSparkSession(sparkConf: SparkConf) =
    SparkSession.builder().config(sparkConf).getOrCreate()

  def withKryo(inital: SparkConf): SparkConf =
    inital
      .set(
        "spark.serializer",
        "org.apache.spark.serializer.KryoSerializer"
      )
      .set("spark.kryo.registrator", classOf[KryoReg].getName())
      .set("spark.kryo.registrationRequired", "true")

  def withAppName(
    initial: SparkConf,
    appName: String
  ): SparkConf =
    initial
      .setAppName(appName)
  //  .set("spark.sql.parquet.enableVectorizedReader", "false")

  def withPerExecutorMemory(initial: SparkConf, mem: String) =
    initial.set("spark.executor.memory", mem)

  def withShufflePartitions(inital: SparkConf, partitions: Int): SparkConf =
    inital.set("spark.sql.shuffle.partitions", partitions.toString())

  def withCaseInsensitive(initial: SparkConf): SparkConf =
    initial.set("spark.sql.caseSensitive", "false")

  def withS3(
    initial:                    SparkConf,
    endpointAccessKeyAndSecret: Option[(String, String, String)]
  ): SparkConf =
    endpointAccessKeyAndSecret
      .map { case (endpoint, accessKey, secretKey) =>
        initial.set("spark.hadoop.fs.s3a.endpoint", endpoint)
        initial.set("spark.hadoop.fs.s3a.access.key", accessKey)
        initial.set("spark.hadoop.fs.s3a.secret.key", secretKey)
      }
      .getOrElse(initial)

}
