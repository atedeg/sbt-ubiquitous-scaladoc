ThisBuild / scalafixDependencies ++= Seq(
  "com.github.liancheng" %% "organize-imports" % "0.6.0",
)

ThisBuild / homepage := Some(url("https://github.com/atedeg/sbt-ubiquitous-scaladoc"))
ThisBuild / organization := "dev.atedeg"
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / versionScheme := Some("early-semver")
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
    "nicolasfara",
    "Nicolas Farabegoli",
    "nicolas.farabegoli@gmail.com",
    url("https://github.com/nicolasfarabegoli"),
  ),
  Developer(
    "vitlinda",
    "Linda Vitali",
    "lindav94vitali@gmail.com",
    url("https://github.com/vitlinda"),
  ),
)

lazy val startupTransition: State => State = { s: State =>
  "conventionalCommits" :: s
}

addCommandAlias("testPlugin", "set ThisBuild / version := \"0.0.0-SNAPSHOT\"; scripted")

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-ubiquitous-scaladoc",
    sbtPlugin := true,
    scriptedBufferLog := false,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq("-Ywarn-unused-import", "-Ypartial-unification"),
    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    libraryDependencies ++= Seq(
      "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
      "net.steppschuh.markdowngenerator" % "markdowngenerator" % "1.3.1.1",
      "com.github.pathikrit" %% "better-files" % "3.9.2",
      "io.circe" %% "circe-yaml" % "0.14.2",
      "io.circe" %% "circe-core" % "0.14.5",
      "io.circe" %% "circe-generic" % "0.14.5",
      "io.circe" %% "circe-parser" % "0.14.5",
      "org.typelevel" %% "cats-core" % "2.9.0",
      "org.scalatest" %% "scalatest" % "3.2.15" % "test",
    ),
    publishTo := sonatypePublishToBundle.value,
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    Global / onLoad := {
      val old = (Global / onLoad).value
      startupTransition compose old
    },
  )
