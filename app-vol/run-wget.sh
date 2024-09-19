#!/bin/bash

target_dir="datasets/nytd"

while IFS= read -r url; do
  # Определяем, какие ссылки нужно переименовать
  if [[ "$url" == *"data.cityofnewyork.us/api/geospatial/d3c5-ddgc?method=export&format=GeoJSON"* ]]; then
    # Скачиваем и переименовываем в указанную директорию
    wget -c -O ${target_dir}/ny_taxi_zones.geojson "$url"
  else
    # Для остальных ссылок скачиваем в указанную директорию без переименования
    wget -c -P ${target_dir} "$url"
  fi
done < datasets.urls


