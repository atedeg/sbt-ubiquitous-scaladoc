import scala.io.Source

ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"

scalaVersion := "3.2.0-RC2"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
    TaskKey[Unit]("checkContent") := {
      val file = Source.fromFile("customTarget/table1.md")
      file.getLines.toList match {
        case List(_header, _line, line1, line2)
            if line1.contains("Example1 doc.")
              && line2.contains("Case1 doc.") =>
          ()
        case _ => sys.error("Table generation error")
      }
    },
  )