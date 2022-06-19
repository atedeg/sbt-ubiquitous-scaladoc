ThisBuild / ubidoc / sourceHtmlDir := file("api/dev/atedeg")
ThisBuild / ubidoc / targetMarkdownDir := file("customTarget")
ThisBuild / ubidoc / tableHeaders := Seq("Term", "Definition")
ThisBuild / ubidoc / htmlTags := Seq("title", "div.doc > p")

scalaVersion := "3.1.3"

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "api"
  )
