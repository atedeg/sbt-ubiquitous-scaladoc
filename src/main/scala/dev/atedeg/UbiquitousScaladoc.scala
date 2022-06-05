package dev.atedeg

import better.files.Dsl.{ ls, SymbolicOperations }
import better.files.FileExtensions
import better.files.File
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL.deepFunctorOps
import net.ruippeixotog.scalascraper.dsl.DSL.*
import net.ruippeixotog.scalascraper.dsl.DSL.Extract.text
import net.steppschuh.markdowngenerator.table.Table

import java.io.{ File => JFile }

object UbiquitousScaladoc {
  private val tableHeaders: Seq[String] = Seq("Term", "Definition")
  private val fileNameSuffix: String = "UbiquitousLanguage.md"
  private val regEx = "[A-Z].*\\.html"

  def apply(sourceDir: JFile, targetDir: JFile): Unit = ubiquitousScaladocTask(sourceDir, targetDir)

  private def ubiquitousScaladocTask(sourceDir: JFile, targetDir: JFile): Unit = for {
    file <- ls(sourceDir.toScala)
    tableBuilder <- generateMarkdownTable()
    (dirName, files) <- extractFilesWithDirName(file)
  } generateMarkdownFile(dirName, files, tableBuilder, targetDir.toScala)

  private def extractFilesWithDirName(file: File): Option[(String, Seq[File])] = file match {
    case f if f.isDirectory => Some((f.name, extractFilesFromAFolder(f)))
    case f if isAScaladocClassFile(f) => Some(("", Seq(f)))
    case _ => None
  }

  private def extractFilesFromAFolder(file: File): Seq[File] = {
    ls(file).collect { case f if isAScaladocClassFile(f) => f }.toSeq
  }

  private def isAScaladocClassFile(file: File): Boolean = file.name matches regEx

  private def extractTextFromHtml(files: Seq[File]): Seq[(String, String)] = for {
    f <- files
    title <- JsoupBrowser().parseFile(f.toJava) >?> text("title")
    doc <- JsoupBrowser().parseFile(f.toJava) >?> text("div.doc > p")
  } yield (title, doc)

  private def generateMarkdownTable(): Option[Table.Builder] = {
    Some(
      new Table.Builder()
        .withAlignment(Table.ALIGN_LEFT)
        .addRow(tableHeaders *),
    )
  }

  private def generateMarkdownFile(
      fileNamePrefix: String,
      files: Seq[File],
      tableBuilder: Table.Builder,
      targetDir: File,
  ): Unit = {
    addRowsToMarkDownTable(files, tableBuilder)
    val file: File = File(s"${targetDir}\\${fileNamePrefix}${fileNameSuffix}")
    file < tableBuilder.build.serialize
  }

  private def addRowsToMarkDownTable(files: Seq[File], tableBuilder: Table.Builder): Unit = {
    val lines: Seq[(String, String)] = extractTextFromHtml(files)
    lines foreach { l => tableBuilder addRow (l._1, l._2) }
  }

}
