ThisBuild / scalafixDependencies ++= Seq(
  "com.github.liancheng" %% "organize-imports" % "0.6.0",
)

ThisBuild / homepage := Some(url("https://github.com/atedeg/sbt-ubiquitous-scaladoc"))
ThisBuild / organization := "dev.atedeg"
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/licenses/MIT"))

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    version := "0.1.0",
    name := "sbt-ubiquitous-scaladoc",
    sbtPlugin := true,
    scriptedBufferLog := false,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += "-Ywarn-unused-import",
    libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.2.1",
  )
