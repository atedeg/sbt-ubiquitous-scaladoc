package dev.atedeg

import java.io.{ File => JFile }

import better.files.Dsl.{ ls, SymbolicOperations }
import better.files.File
import better.files.FileExtensions
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.text
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.deepFunctorOps
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.table.Table.Builder

object Ubidoc {
  private val fileNameSuffix: String = "UbiquitousLanguage"
  private val fileNameExtension: String = ".md"
  private val scaladocFileNameRegex = "[A-Z].*\\.html"

  def apply(sourceDir: JFile, targetDir: JFile, htmlTags: Seq[String], tableHeaders: Seq[String]): Unit =
    Internals.ubiquitousScaladocTask(sourceDir, targetDir, htmlTags, tableHeaders)

  private object Internals {

    def ubiquitousScaladocTask(
        sourceDir: JFile,
        targetDir: JFile,
        htmlTags: Seq[String],
        tableHeaders: Seq[String],
    ): Unit = {
      if (htmlTags.length != tableHeaders.length)
        throw new IllegalArgumentException("htmlTags and tableHeaders must have the same number of elements")
      for {
        dir <- directoriesFromDir(sourceDir.toScala)
        rows = rowsFromFiles(dir, htmlTags)
      } generateMarkdownFile(dir.name, rows, targetDir.toScala, tableHeaders)
    }

    def directoriesFromDir(sourceDir: File): Iterator[File] = ls(sourceDir) filter (_.isDirectory)

    def rowsFromFiles(dir: File, htmlTags: Seq[String]): Iterator[Seq[String]] =
      scaladocFilesFromDir(dir) map (extractEntriesFromFile(_, htmlTags))

    def scaladocFilesFromDir(dir: File): Iterator[File] = ls(dir) filter isScaladocClassFile

    def isScaladocClassFile(file: File): Boolean = file.name matches scaladocFileNameRegex

    def extractEntriesFromFile(file: File, htmlTags: Seq[String]): Seq[String] = {
      val document = JsoupBrowser() parseFile file.toJava
      htmlTags flatMap (document >?> text(_))
    }
  }

  def tableBuilder(tableHeaders: Seq[String]): Builder =
    new Builder() withAlignment Table.ALIGN_LEFT addRow (tableHeaders *)

  def generateMarkdownFile(
      dirName: String,
      rows: Iterator[Seq[String]],
      targetDir: File,
      tableHeaders: Seq[String],
  ): Unit = {
    val table = addConceptsToTable(rows, tableHeaders)
    val file = targetDir / s"$dirName$fileNameSuffix$fileNameExtension"
    file < table.serialize
  }

  def addConceptsToTable(rows: Iterator[Seq[String]], tableHeaders: Seq[String]): Table = {
    val builder: Builder = tableBuilder(tableHeaders)
    for { row <- rows } builder.addRow(row *)
    builder.build()
  }
}
