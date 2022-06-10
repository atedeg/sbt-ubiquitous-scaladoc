package dev.atedeg

import better.files.Dsl.{ ls, SymbolicOperations }
import better.files.FileExtensions
import better.files.File
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.deepFunctorOps
import net.ruippeixotog.scalascraper.dsl.DSL.*
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.text
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.table.Table.Builder

import java.io.{ File => JFile }

object UbiquitousScaladoc {
  private val tableHeaders: Seq[String] = Seq("Term", "Definition")
  private val fileNameSuffix: String = "UbiquitousLanguage"
  private val fileNameExtension: String = ".md"
  private val scaladocFileNameRegex = "[A-Z].*\\.html"

  def apply(sourceDir: JFile, targetDir: JFile): Unit = Internals.ubiquitousScaladocTask(sourceDir, targetDir)

  private object Internals {
    final case class Concept(name: String, description: String)

    def ubiquitousScaladocTask(sourceDir: JFile, targetDir: JFile): Unit = for {
      dir <- directoryFromSource(sourceDir.toScala)
      concepts = conceptFromFiles(dir)
    } generateMarkdownFile(dir.name, concepts, targetDir.toScala)

    def directoryFromSource(sourceDir: File): Iterator[File] = ls(sourceDir) filter (_.isDirectory)

    def conceptFromFiles(dir: File): Iterator[Concept] = scaladocFilesFromDir(dir) flatMap extractTextFromHtml

    def scaladocFilesFromDir(dir: File): Iterator[File] = ls(dir) filter isScaladocClassFile

    def isScaladocClassFile(file: File): Boolean = file.name matches scaladocFileNameRegex

    def extractTextFromHtml(file: File): Option[Concept] = {
      val document = JsoupBrowser() parseFile file.toJava
      for {
        title <- document >?> text("title")
        doc <- document >?> text("div.doc > p")
      } yield Concept(title, doc)
    }

    def tableBuilder: Builder = new Builder() withAlignment Table.ALIGN_LEFT addRow(tableHeaders *)

    def generateMarkdownFile(dirName: String, concepts: Iterator[Concept], targetDir: File): Unit = {
      val table = addConceptsToTable(concepts)
      val file: File = targetDir / s"$dirName$fileNameSuffix$fileNameExtension"
      file < table.serialize
    }

    def addConceptsToTable(concepts: Iterator[Concept]): Table = {
      val builder = tableBuilder
      for { Concept(name, description) <- concepts } builder.addRow(name, description)
      builder.build()
    }
  }
}
