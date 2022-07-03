package dev.atedeg

import java.io.{ File => JFile }

object Ubidoc {
  def apply(workingDir: JFile, targetDir: JFile): Unit = Internals.ubiquitousScaladocTask(workingDir, targetDir)

  private object Internals {

    def ubiquitousScaladocTask(workingDir: JFile, targetDir: JFile): Unit = ???


  }
}
