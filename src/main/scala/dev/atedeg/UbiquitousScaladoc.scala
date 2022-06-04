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

  def apply(sourceDir: JFile, targetDir: JFile): Unit = ubiquitousScaladocTask(sourceDir, targetDir)

  private def ubiquitousScaladocTask(sourceDir: JFile, targetDir: JFile): Unit = {
    extractFilesWithDirName(sourceDir.toScala) foreach { s =>
      {
        val tableBuilder = generateMarkdownTable()
        val (dirName, files) = s
        val lines = extractTextFromHtml(files)
        lines.collect { case Some((t, d)) => tableBuilder addRow (t, d) }
        generateMarkdownFile(dirName, tableBuilder, targetDir.toScala)
      }
    }
  }

  private def extractFilesWithDirName(file: File): Seq[(String, Seq[File])] = file match {
    case f if f.isDirectory =>
      ls(file).collect {
        case f if f.isDirectory => (f.name, ls(f).toSeq)
        case s => ("", Seq(s))
      }.toSeq
    case f => Seq((f.name, Seq(f)))
  }

  private def extractTextFromHtml(files: Seq[File]): Seq[Option[(String, String)]] = {
    files.map { f =>
      val document = JsoupBrowser().parseFile(f.toJava)
      val title: Option[String] = document >?> text("title")
      val doc: Option[String] = document >?> text("div.doc > p")
      (title, doc) match {
        case (Some(t), Some(d)) => Some(t, d)
        case _ => None
      }
    }
  }

  private def generateMarkdownTable(): Table.Builder = {
    new Table.Builder()
      .withAlignment(Table.ALIGN_LEFT)
      .addRow(tableHeaders*)
  }

  private def generateMarkdownFile(fileNamePrefix: String, tableBuilder: Table.Builder, targetDir: File): Unit = {
    val file: File = targetDir / s"${fileNamePrefix}${fileNameSuffix}"
    file < tableBuilder.build.serialize
  }
}
