ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

scalaVersion := "3.6.1"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
  )
