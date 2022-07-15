package dev.atedeg

import sbt._

trait UbiquitousScaladocKeys {

  val ubidoc: TaskKey[Unit] =
    taskKey[Unit]("Task for creating a Ubiquitous Language markdown table out of a Scaladoc html file")

  val lookupDirectory: SettingKey[File] =
    settingKey[File]("The directory where to look for the html files")

  val targetDirectory: SettingKey[File] =
    settingKey[File]("The target directory where to put the generated Markdown tables")
}
