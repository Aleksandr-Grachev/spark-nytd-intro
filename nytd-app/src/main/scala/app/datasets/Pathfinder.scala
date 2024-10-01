package app.datasets

import java.net.URI

trait Pathfinder {

  def datasetDir: String

  def getDatasetAbsolutePathURI(pFileName: String) =
    s"${datasetDir}/$pFileName"

  def getDatasetAbsolutePath(pFileName: String) = {
    val uri = URI.create(datasetDir)
    s"${uri.getPath()}/$pFileName"
  }

  def forYearA(dsName: String)(yyyy: String): IndexedSeq[(Int, String)] =
    12 to 1 by -1 map { i =>
      i -> f"${dsName}_${yyyy}-$i%02d.parquet"
    }

  def forYearB(dsName: String)(yyyy: String): IndexedSeq[String] =
    forYearA(dsName)(yyyy).map(_._2)

}
