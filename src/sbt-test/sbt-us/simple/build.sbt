ThisBuild / ubiquitousScaladoc / usSourceHtmlDir := file("api")
ThisBuild / ubiquitousScaladoc / usTargetMarkdownDir := file("customTarget")

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "api/generated"
  )
