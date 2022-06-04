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
  private val tableFirstRow: (String, String) = ("Term", "Definition")
  private val fileSuffix: String = "UbiquitousLanguage.md"

  def apply(sourceDir: JFile, targetDir: JFile): Unit = ubiquitousScaladocTask(sourceDir, targetDir)

  private def ubiquitousScaladocTask(sourceDir: JFile, targetDir: JFile): Unit = {
    extractFilesWithDirName(sourceDir.toScala).foreach { s =>
      {
        val tableBuilder = generateMarkdownTable()
        val dirName = s._1
        val files = s._2
        val lines = extractTextFromHtml(files)
        lines.foreach { l =>
          tableBuilder addRow (l._1, l._2)
        }
        generateMarkdownFile(dirName, tableBuilder, targetDir.toScala)
      }
    }
  }

  private def extractFilesWithDirName(file: File): Seq[(String, Seq[File])] = {
    file match {
      case f if f.isDirectory =>
        ls(file).collect {
          case f if f.isDirectory => (f.name, ls(f).toSeq)
        }.toSeq
      case f => Seq((f.name, Seq(f)))
    }
  }

  private def extractTextFromHtml(files: Seq[File]): Seq[(String, String)] = {
    files.map { f =>
      val doc = JsoupBrowser().parseFile(f.toJava)
      (doc >> text("title"), doc >> text("div.doc > p"))
    }
  }

  private def generateMarkdownTable(): Table.Builder = {
    new Table.Builder()
      .withAlignment(Table.ALIGN_LEFT)
      .addRow(tableFirstRow._1, tableFirstRow._2)
  }

  private def generateMarkdownFile(filePrefix: String, tableBuilder: Table.Builder, targetDir: File) = {
    val table = tableBuilder.build()
    val file = File(s"${targetDir}${filePrefix}${fileSuffix}")
    file < table.serialize()
  }
}
