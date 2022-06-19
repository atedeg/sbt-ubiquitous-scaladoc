package dev.atedeg

import sbt._

trait UbiquitousScaladocKeys {

  val ubidoc: TaskKey[Unit] =
    taskKey[Unit]("Task for creating a Ubiquitous Language markdown table out of a Scaladoc html file")

  val htmlTags: SettingKey[Seq[String]] =
    settingKey[Seq[String]]("The tags to be extracted from the html file")

  val tableHeaders: SettingKey[Seq[String]] =
    settingKey[Seq[String]]("The headers of the markdown table")

  val fileNameSuffix: SettingKey[String] =
    settingKey[String]("The suffix of the generated file's name")

  val sourceHtmlDir: SettingKey[File] =
    settingKey[File]("The source directory to generate ubiquitous language markdown tables from")

  val targetMarkdownDir: SettingKey[File] =
    settingKey[File]("The target directory where to put the generated ubiquitous language markdown tables")
}
