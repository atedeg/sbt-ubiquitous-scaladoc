ThisBuild / ubiquitousScaladoc / usSourceHtmlDir := file("api")
ThisBuild / ubiquitousScaladoc / usTargetMarkdownDir := file("targetCustom")

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "api"
  )
