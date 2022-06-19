package dev.atedeg

import java.io.{ File => JFile }

import better.files.Dsl.{SymbolicOperations, ls}
import better.files.File
import better.files.FileExtensions
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.text
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.deepFunctorOps
import net.steppschuh.markdowngenerator.table.Table
import net.steppschuh.markdowngenerator.table.Table.Builder

object UbiquitousScaladoc {
  private val tableHeaders: Seq[String] = Seq("Term", "Definition")
  private val htmlTags: Option[(String, String)] = Some("title", "div.doc > p")
  private val fileNameSuffix: String = "UbiquitousLanguage"
  private val fileNameExtension: String = ".md"
  private val scaladocFileNameRegex = "[A-Z].*\\.html"

  def apply(sourceDir: JFile, targetDir: JFile): Unit = Internals.ubiquitousScaladocTask(sourceDir, targetDir)

  private object Internals {
    final case class Concept(name: String, description: String)

    def ubiquitousScaladocTask(sourceDir: JFile, targetDir: JFile): Unit = for {
      dir <- directoriesFromDir(sourceDir.toScala)
      concepts = conceptsFromFiles(dir)
    } generateMarkdownFile(dir.name, concepts, targetDir.toScala)

    def directoriesFromDir(sourceDir: File): Iterator[File] = ls(sourceDir) filter (_.isDirectory)

    def conceptsFromFiles(dir: File): Iterator[Concept] = scaladocFilesFromDir(dir) flatMap (extractConceptFromFile(_).toList)

    def scaladocFilesFromDir(dir: File): Iterator[File] = ls(dir) filter isScaladocClassFile

    def isScaladocClassFile(file: File): Boolean = file.name matches scaladocFileNameRegex

    def extractConceptFromFile(file: File): Option[Concept] = {
      val document = JsoupBrowser() parseFile file.toJava
      for {
        (t1, t2) <- htmlTags
        name <- document >?> text(t1)
        description <- document >?> text(t2)
      } yield Concept(name, description)
    }

    def tableBuilder: Builder = new Builder() withAlignment Table.ALIGN_LEFT addRow (tableHeaders *)

    def generateMarkdownFile(dirName: String, concepts: Iterator[Concept], targetDir: File): Unit = {
      val table = addConceptsToTable(concepts)
      val file = targetDir / s"$dirName$fileNameSuffix$fileNameExtension"
      file < table.serialize
    }

    def addConceptsToTable(concepts: Iterator[Concept]): Table = {
      val builder: Builder = tableBuilder
      for { Concept(name, description) <- concepts } builder.addRow(name, description)
      builder.build()
    }
  }
}
