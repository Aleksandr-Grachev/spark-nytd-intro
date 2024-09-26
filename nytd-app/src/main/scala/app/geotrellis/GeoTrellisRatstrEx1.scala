package app.geotrellis

import geotrellis.proj4.CRS
import geotrellis.raster.io.geotiff.GeoTiff

object GeoTrellisRatstrEx1 extends App {
  import geotrellis.raster.Tile
  import geotrellis.raster.IntArrayTile
  import geotrellis.raster.Raster

  import geotrellis.raster.ColorMap
  import geotrellis.vector._

// Определяем Extent для области Нью-Йорка (пример)
  val nycExtent = Extent(
    xmin = -74.05, // Долгота левой границы
    ymin = 40.68, // Широта нижней границы
    xmax = -73.85, // Долгота правой границы
    ymax = 40.85 // Широта верхней границы
  )

// Размеры растра (например, 100x100 пикселей)
  val cols = 100
  val rows = 100

// Создаем растровую сетку (Tile), где каждая ячейка содержит количество поездок
// Допустим, у нас есть данные о числе поездок по зонам, их можно заложить в ячейки
  val taxiTripCounts = Array.fill(cols * rows)(0) // Массив для хранения данных

// Заполняем данные для растра (в реальном мире данные могут быть получены из источников, таких как паркет-файлы с такси поездками)
  for (i <- 0 until cols * rows) {
    taxiTripCounts(i) =
      scala.util.Random.nextInt(100) // Генерируем случайные данные для примера
  }

// Создаем Tile на основе данных о поездках
  val tile: Tile = IntArrayTile(taxiTripCounts, cols, rows)

// Привязываем растр к реальной географической области (Extent)
  val raster = Raster(tile, nycExtent)

// Можно визуализировать количество поездок, например, с помощью цветовой схемы
  val colorMap = ColorMap(
    Map(
      0   -> 0xffffff00, // Белый цвет для 0 поездок
      10  -> 0xffddddff, // Светло-синий для небольшого количества поездок
      50  -> 0xff8888ff, // Среднее количество поездок
      100 -> 0xff0000ff // Красный цвет для максимума поездок
    )
  )

// Рендерим растр в изображение
  val renderedImage = tile.renderPng(colorMap)
  renderedImage.write("nyc_taxi_trips.png")

  //Создаем CRS (систему координат) для привязки к реальной карте
  val crs = CRS.fromEpsgCode(4326) // WGS84

  //Сохраним растр как GeoTIFF для последующего анализа
  GeoTiff(raster, crs).write("nyc_taxi_trip_counts.tif")

  println(
    "Raster with taxi trip counts created and saved as nyc_taxi_trip_counts.tif"
  )

}
