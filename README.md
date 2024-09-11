# Project spark-intro

## Description
Это тестовый проект. Проект включает в себя приложение для Apache Spark для расчета статистики по публичным наборам данных от [TLC Trip Record Data](https://www.nyc.gov/site/tlc/about/tlc-trip-record-data.page)


## Project Goals

- [ ] Понятная структура проекта
- [ ] Описание через README.md
- [ ] Тесты
- [ ] Предусмотреть конфигурирование джобы через файл конфига application.conf 
  - [X] библиотека pureConfig и переменные окружения
  - [X] имя джобы,
  - [ ] имя файла источника данных
  - [ ] имя файла с результатами обработки
  - [X] изменение уровня логирования
- [X] Добавить дополнительное логирование, вывести в лог конфиг, с которым запускалась джоба
- [ ] Чистый код, комментарии, логи, там где нужно
- [ ] Организован сбор метрик и отображение в Grafana
- [ ] docker-compose с необходимым окружением
    - [X] spark-master
    - [X] spark-worker
    - [ ] prometheus 
    - [ ] grafana
    - [ ] db?
    - [ ] hdfs?
- [ ] Обязательно использование функций join, groupBy, agg
- [X] Получить fat-jar файл джобы, для запуска на кластере с помощью spark-submit
    - [X] использовать плагин sbt-assembly

## Project Tasks

1. Анализ популярности районов:
    
    Задача: Определи самые популярные районы для посадки и высадки пассажиров. Для этого можно использовать Taxi Zone IDs и подсчитать количество поездок, начинающихся и заканчивающихся в каждом районе.
    Результат: Рейтинг районов по популярности для посадки и высадки.

## Getting started

Для вашего удобства, перед началом работы проверьте, что в вашей системе установлены следующие инструменты

* [Docker](https://www.docker.com/)
* [sdkman](https://sdkman.io/)

### Getting NYTD dataset
Перед началом работы необходимо скачать [^1] TLC Trip Record Data sets:
```shell
>cd app-vol/datasets/nytd 
>wget -ci datasets.urls
```

Измените права на запись для каталогов, это нужно чтобы приложение находяcь в контейнере докера могло туда сохранять данные.

```shell
>cd <project-root>
>chmod -R a+w app-vol/
```


[^1]: Проект разрабатывался на операционной системе Linux, и здесь и далее используются команды для нее.

### Getting Apache Spark

Если у вас не установлен Apache Spark, необходимо его [скачать](https://spark.apache.org/downloads.html) и разархивировать, к примеру в каталог `/opt/spark/`

### Adding your files
Для настройки запуска приложения вы можете добавить свой собственный `.env` файл, 
к примеру

```shell
SPARK_HOME=/opt/spark/351
JAR_VERSION=0.0.1
#use this if you have a remote Spark cluster
#SPARK_DRIVER_PORT=7777
#SPARK_DRIVER_HOST=192.168.5.3
# local
#SPARK_MASTER=local[*]
#docker
SPARK_MASTER=spark://0.0.0.0:7077
```

### Running tools

Находясь в корне проекта, настройте перeменные среды и запустите докер

```shell
>sdk env install && sdk env 
>docker compose up -d
```

Соберите проект
```shell
>sbt clean assembly
```

### Testing and Deploying

Запуск проекта происходит при помощи утилиты `spark-submit`, которая обернута скриптом `run.sh`

Находясь в корне проекта, запустите

```shell
>./run.sh
```

Проект имеет два режима запуска 
- Main TODO: собирает и показывает статистику по наборам данных NYTD
- Samples - TODO: собирает samples для создания тестовых наборов данных

### Logging

Приложение в своей работе использует настроки логирования через файл `log4j2.xml` в корне проекта.  Запуск тестов использует `log4j2-test.xml` в тестовых ресурсах проекта.

### Results
  
  TODO: iml

## Conclusion

Проект нуждается в вашем feedback. So please create an issue. 
Вы также можете отметить Project Goals в этом документе, которые, по вашему мнению, выполнены.
