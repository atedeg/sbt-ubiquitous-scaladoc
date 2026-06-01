ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

scalaVersion := "3.8.4"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
  )
