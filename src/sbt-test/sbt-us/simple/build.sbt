ThisBuild / ubiquitousScaladoc / sourceHtmlDir := file("api")
ThisBuild / ubiquitousScaladoc / targetMarkdownDir := file("customTarget")

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "api" / "generated"
  )
