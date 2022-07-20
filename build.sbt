ThisBuild / scalafixDependencies ++= Seq(
  "com.github.liancheng" %% "organize-imports" % "0.6.0",
)

ThisBuild / homepage := Some(url("https://github.com/atedeg/sbt-ubiquitous-scaladoc"))
ThisBuild / organization := "dev.atedeg"
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / wartremoverErrors ++= Warts.allBut(Wart.Throw, Wart.Recursion)

ThisBuild / developers := List(
  Developer(
    "giacomocavalieri",
    "Giacomo Cavalieri",
    "giacomo.cavalieri@icloud.com",
    url("https://github.com/giacomocavalieri"),
  ),
  Developer(
    "ndido98",
    "Nicolò Di Domenico",
    "ndido98@gmail.com",
    url("https://github.com/ndido98"),
  ),
  Developer(
    "vitlinda",
    "Linda Vitali",
    "lindav94vitali@gmail.com",
    url("https://github.com/vitlinda"),
  ),
)

resolvers += "jitpack" at "https://jitpack.io"

lazy val startupTransition: State => State = { s: State =>
  "conventionalCommits" :: s
}

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-ubiquitous-scaladoc",
    version := "1.0.0-beta.3",
    sbtPlugin := true,
    scriptedBufferLog := false,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq("-Ywarn-unused-import", "-Ypartial-unification"),
    libraryDependencies ++= Seq(
      "net.ruippeixotog" %% "scala-scraper" % "2.2.1",
      "com.github.Steppschuh" %% "Java-Markdown-Generator" % "1.3.2",
      "com.github.pathikrit" %% "better-files" % "3.9.1",
      "io.circe" %% "circe-yaml" % "0.14.1",
      "io.circe" %% "circe-core" % "0.14.2",
      "io.circe" %% "circe-generic" % "0.14.2",
      "io.circe" %% "circe-parser" % "0.14.2",
      "org.typelevel" %% "cats-core" % "2.7.0",
      "org.scalatest" %% "scalatest" % "3.2.12" % "test",
    ),
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    Global / onLoad := {
      val old = (Global / onLoad).value
      startupTransition compose old
    },
  )
