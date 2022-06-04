package dev.atedeg

import sbt.{ AutoPlugin, Setting }

object UbiquitousScaladocPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends UbiquitousScaladocKeys

  import autoImport.*

  override def projectSettings: Seq[Setting[_]] = Seq(
    ubiquitousScaladoc := UbiquitousScaladoc(usSourceHtmlDir.value, usTargetMarkdownDir.value)
  )

}