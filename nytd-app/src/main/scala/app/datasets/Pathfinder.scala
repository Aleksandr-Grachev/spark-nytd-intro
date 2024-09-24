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

}
