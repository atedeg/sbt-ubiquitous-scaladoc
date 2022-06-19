ThisBuild / ubidoc / sourceHtmlDir := file("api")
ThisBuild / ubidoc / targetMarkdownDir := file("customTarget")

lazy val root = (project in file("."))
  .settings(
    Compile / doc / target := baseDirectory.value / "api" / "generated"
  )
