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
      file <- ls(sourceDir.toScala)
      tableBuilder <- generateMarkdownTable()
      (dirName, files) <- extractFilesWithDirName(file)
    } generateMarkdownFile(dirName, files, tableBuilder, targetDir.toScala)

    def extractFilesWithDirName(file: File): Option[(String, Seq[File])] = file match {
      case f if f.isDirectory => Some((f.name, extractFilesFromAFolder(f)))
      case f if isAScaladocClassFile(f) => Some(("", Seq(f)))
      case _ => None
    }

    def extractFilesFromAFolder(file: File): Seq[File] = {
      ls(file).collect { case f if isAScaladocClassFile(f) => f }.toSeq
    }

    def isAScaladocClassFile(file: File): Boolean = file.name matches scaladocFileNameRegex

    def extractTextFromHtml(files: Seq[File]): Seq[Concept] = for {
      f <- files
      name <- JsoupBrowser().parseFile(f.toJava) >?> text("title")
      description <- JsoupBrowser().parseFile(f.toJava) >?> text("div.doc > p")
    } yield Concept(name, description)

    def generateMarkdownTable(): Option[Builder] = {
      Some(
        new Builder()
          .withAlignment(Table.ALIGN_LEFT)
          .addRow(tableHeaders *),
      )
    }

    def generateMarkdownFile(
        dirName: String,
        files: Seq[File],
        tableBuilder: Builder,
        targetDir: File,
    ): Unit = {
      addRowsToMarkDownTable(files, tableBuilder)
      val file: File = targetDir / s"$dirName$fileNameSuffix$fileNameExtension"
      file < tableBuilder.build.serialize
    }

    def addRowsToMarkDownTable(files: Seq[File], tableBuilder: Builder): Unit = {
      val lines: Seq[Concept] = extractTextFromHtml(files)
      lines foreach { l => tableBuilder addRow (l.name, l.description) }
    }
  }
}
