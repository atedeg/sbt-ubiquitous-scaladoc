import scala.io.Source

ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
    TaskKey[Unit]("checkContent") := {
      val file = Source.fromFile("customTarget/table1.md")
      file.getLines.toList match {
       case List(_header, _line, line1, line2)
         if line1.contains("Enum1.Case1")
         && line2.contains("Enum2.Case1") => ()
       case _ => sys.error("Table generation error")
      }
    },
  )
