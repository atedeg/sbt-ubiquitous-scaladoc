package dev.atedeg

import sbt.*

trait UbiquitousScaladocKeys {
  val ubiquitousScaladoc: TaskKey[Unit] = taskKey[Unit]("Creating an Ubiquitous Language markdown table out of a Scaladoc html file")

  val usSourceHtmlDir: SettingKey[File] = settingKey[File]("Source directory to generate ubiquitous language markdown table from")

  val usTargetMarkdownDir: SettingKey[File] = settingKey[File]("Target directory to store generated ubiquitous language markdown table")

}
