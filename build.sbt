import sbtassembly.AssemblyPlugin

ThisBuild / organization := "ru.neoflex"
ThisBuild / version := "0.0.1"
ThisBuild / scalaVersion := "2.12.20"

lazy val compileSettings =
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:implicitConversions",
    "-language:existentials",
    "-language:higherKinds",
    "-Ywarn-unused",
    "-Ywarn-dead-code"
  )

fork in Test := true

javaOptions ++= Seq(
  "-Xms8G",
  "-Xmx8G",
  "-XX:MaxPermSize=4048M",
  "-XX:+CMSClassUnloadingEnabled"
)

lazy val sparkNYTDApp = (project in file("nytd-app"))
  .settings(compileSettings)

lazy val root = (project in file("."))
  .aggregate(
    sparkNYTDApp
  )
