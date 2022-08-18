import scala.io.Source

ThisBuild / ubidoc / targetDirectory := baseDirectory.value / "customTarget"
ThisBuild / ubidoc / lookupDirectory := target.value / "site"
ThisBuild / ubidoc / linkSolver := ((_: String) => "fixed-link")

scalaVersion := "3.2.0-RC2"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "target/site",
    TaskKey[Unit]("checkContent") := {
      val file = Source.fromFile("customTarget/table1.md")
      file.getLines.toList match {
        case List(_header, _line, line1) if line1.contains("Link to [linked](fixed-link)") => ()
        case _ => sys.error("Table generation error")
      }
    },
  )
