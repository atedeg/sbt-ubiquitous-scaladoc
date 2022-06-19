package dev.atedeg

import sbt.{AutoPlugin, Setting, Task}

object UbiquitousScaladocPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends UbiquitousScaladocKeys

  import autoImport.*

  override lazy val buildSettings: Seq[Setting[Task[Unit]]] = Seq(
    ubiquitousScaladoc := UbiquitousScaladoc(
      (ubiquitousScaladoc / sourceHtmlDir).value,
      (ubiquitousScaladoc / targetMarkdownDir).value,
    ),
  )

}
