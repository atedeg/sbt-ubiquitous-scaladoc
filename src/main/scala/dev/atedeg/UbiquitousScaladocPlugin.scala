package dev.atedeg

import sbt.{ AutoPlugin, Setting }

object UbiquitousScaladocPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends UbiquitousScaladocKeys

  import autoImport.*

  override lazy val buildSettings: Seq[Setting[_]] = Seq(
    ubidoc := Ubidoc(
      (ubidoc / workingDirectory).value,
      (ubidoc / targetDirectory).value,
    ),
  )

}
