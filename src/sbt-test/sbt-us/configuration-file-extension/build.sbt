import scala.io.Source

ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

scalaVersion := "3.6.1"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
    TaskKey[Unit]("checkContent") := {
      val file = Source.fromFile("customTarget/table1.md")
      file.getLines.toList match {
        case List(_header, _line, line1)
          if line1.contains("Custom name")
          && line1.contains("Example doc.") => ()
        case _ => sys.error("Table generation error")
      }
    },
  )
