package dev.atedeg

import sbt._

trait UbiquitousScaladocKeys {

  val ubiquitousScaladoc: TaskKey[Unit] =
    taskKey[Unit]("Task for creating a Ubiquitous Language markdown table out of a Scaladoc html file")

  val sourceHtmlDir: SettingKey[File] =
    settingKey[File]("The source directory to generate ubiquitous language markdown tables from")

  val targetMarkdownDir: SettingKey[File] =
    settingKey[File]("The target directory where to put the generated ubiquitous language markdown tables")
}
