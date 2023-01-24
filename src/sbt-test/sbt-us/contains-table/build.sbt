import scala.io.Source

ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
    TaskKey[Unit]("checkContent") := {
      val file = Source.fromFile("customTarget/table1.md")
      val table = "| Term    | Definition    || ------- | ------------- || Example | Example doc.  |"
      val text = file.getLines.mkString
      if(text != table) sys.error("Table generation error")
      ()
    },
  )
