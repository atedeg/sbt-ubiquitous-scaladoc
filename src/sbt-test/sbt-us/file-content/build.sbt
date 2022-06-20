import scala.io.Source
import scala.util.Using

ThisBuild / ubidoc / sourceHtmlDir := file("api/dev/atedeg")
ThisBuild / ubidoc / targetMarkdownDir := file("customTarget")
ThisBuild / ubidoc / tableHeaders := Seq("Term", "Definition")
ThisBuild / ubidoc / htmlTags := Seq("title", "div.doc > p")

scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "api",
    TaskKey[Unit]("check") := {
      Using(Source.fromFile("customTarget/exampleUbiquitousLanguage.md")) { file =>
        val table =
          "| Term     | Definition    || -------- | ------------- || Example  | Example doc.  || Example2 | Example2 doc. |"
        val text = file.getLines()
        if (!text.mkString.contains(table)) sys.error("Table generation error")
        ()
      }
    },
  )
