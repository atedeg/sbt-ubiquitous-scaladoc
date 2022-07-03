ThisBuild / scalafixDependencies ++= Seq(
  "com.github.liancheng" %% "organize-imports" % "0.6.0",
)

ThisBuild / homepage := Some(url("https://github.com/atedeg/sbt-ubiquitous-scaladoc"))
ThisBuild / organization := "dev.atedeg"
ThisBuild / licenses := List("MIT" -> url("https://opensource.org/licenses/MIT"))
ThisBuild / wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Nothing, Wart.Equals, Wart.Throw, Wart.SizeIs, Wart.Option2Iterable)
ThisBuild / publishTo := sonatypePublishToBundle.value
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

resolvers += "jitpack" at "https://jitpack.io"

lazy val startupTransition: State => State = { s: State =>
  "conventionalCommits" :: s
}

lazy val root = project
  .in(file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    version := "1.0.0-beta.3",
    name := "sbt-ubiquitous-scaladoc",
    sbtPlugin := true,
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += "-Ywarn-unused-import",
    libraryDependencies ++= Seq(
      "net.ruippeixotog" %% "scala-scraper" % "2.2.1",
      "com.github.Steppschuh" %% "Java-Markdown-Generator" % "1.3.2",
      "com.github.pathikrit" %% "better-files" % "3.9.1",
    ),
    Global / onLoad := {
      val old = (Global / onLoad).value
      startupTransition compose old
    },
  )
