package dev.atedeg

import sbt.{ AutoPlugin, Setting }

object UbiquitousScaladocPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends UbiquitousScaladocKeys

  import autoImport.*

  override lazy val buildSettings: Seq[Setting[_]] = Seq(
    ubiquitousScaladoc := UbiquitousScaladoc(
      (ubiquitousScaladoc / usSourceHtmlDir).value,
      (ubiquitousScaladoc / usTargetMarkdownDir).value,
    ),
  )

}
