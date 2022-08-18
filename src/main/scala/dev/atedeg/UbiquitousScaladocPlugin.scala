package dev.atedeg

import sbt.Keys.{ baseDirectory, streams }
import sbt.{ AutoPlugin, Setting }

@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
object UbiquitousScaladocPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends UbiquitousScaladocKeys

  import autoImport.*

  override lazy val buildSettings: Seq[Setting[_]] = Seq(
    ubidoc / linkSolver := ((s: String) => s),
    ubidoc := Ubidoc(
      (ubidoc / lookupDirectory).value,
      (ubidoc / targetDirectory).value,
      (ubidoc / baseDirectory).value,
      (ubidoc / linkSolver).value,
      streams.value.log,
    ),
  )
}
