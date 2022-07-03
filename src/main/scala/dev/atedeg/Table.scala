package dev.atedeg

import scala.util.Try

import dev.atedeg.Selector.toFiles

import better.files.File
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import better.files.Dsl.SymbolicOperations
import cats.implicits.*
import net.steppschuh.markdowngenerator.table.Table.ALIGN_LEFT
import net.steppschuh.markdowngenerator.table.Table.Builder

final case class Row(columns: List[String])

final case class Table(title: String, columnsName: List[String], rows: List[Row]) {

  override def toString: String = {
    val builder = new Builder().withAlignment(ALIGN_LEFT).addRow(columnsName *)
    for { Row(columns) <- rows } builder.addRow(columns *)
    builder.build.serialize
  }

  def serialize(targetDir: File): Try[Unit] = {
    val file = targetDir / title / ".md"
    Try(file < this.toString)
  }
}

object Table {

  def parse(workingDir: File, tableConfig: TableConfig, ignoredFiles: Set[File]): Either[String, (Table, List[File])] =
    for {
      files <- tableConfig.rows.flatTraverse(toFiles(workingDir, _)).map(_.filterNot(ignoredFiles.contains))
      rows <- files.traverse(parseFile(_, tableConfig.columns))
      columnNames = tableConfig.columns.map(_.name)
    } yield (Table(tableConfig.name, columnNames, rows), files)

  private def parseFile(file: File, columnConfigs: List[ColumnConfig]): Either[String, Row] = {
    val document = JsoupBrowser() parseFile file.toJava
    columnConfigs.map(HtmlParsing.extractColumn(document, _)).sequence.map(Row)
  }
}
