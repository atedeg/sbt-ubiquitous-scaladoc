package dev.atedeg

import sbt.Keys.baseDirectory
import sbt.{ AutoPlugin, Setting, Task }

@SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.Nothing"))
object UbiquitousScaladocPlugin extends AutoPlugin {

  override def trigger = allRequirements

  object autoImport extends UbiquitousScaladocKeys

  import autoImport.*

  override lazy val buildSettings: Seq[Setting[Task[Unit]]] = Seq(
    ubidoc := Ubidoc(
      (ubidoc / lookupDirectory).value,
      (ubidoc / targetDirectory).value,
      (ubidoc / baseDirectory).value,
    ),
  )
}
