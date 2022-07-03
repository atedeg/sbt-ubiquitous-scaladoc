package dev.atedeg

import better.files.File
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import better.files.Dsl.SymbolicOperations
import cats.implicits.*
import net.steppschuh.markdowngenerator.table.Table.ALIGN_LEFT
import net.steppschuh.markdowngenerator.table.Table.Builder

final case class Table(title: String, columnsName: List[String], rows: List[Row]) {
  override def toString: String = {
    val builder = new Builder().withAlignment(ALIGN_LEFT).addRow(columnsName*)
    for { Row(columns) <- rows } builder.addRow(columns*)
    builder.build.serialize
  }

  def serialize(targetDir: File): Unit = {
    val file = targetDir / title / ".md"
    file < this.toString
  }
}

final case class Row(columns: List[String])

object Table {
  import dev.atedeg.Selectors.toFiles
  def parse(workingDir: File, tableConfig: TableConfig): Either[String, (Table, List[File])] = for {
    files <- tableConfig.rows.flatMap(toFiles(workingDir, _)).asRight
    rows <- files.map(parseFile(_, tableConfig.columns)).sequence
    columnNames = tableConfig.columns.map(_.name)
  } yield (Table(tableConfig.name, columnNames, rows), files)

  private def parseFile(file: File, columnConfigs: List[ColumnConfig]): Either[String, Row] = {
    val document = JsoupBrowser() parseFile file.toJava
    columnConfigs.map(HtmlParsing.extractColumn(document, _)).sequence.map(Row)
  }
}

object Selectors {
  def toFiles(workingDir: File, selector: Selector): List[File] = {
    val file = File(workingDir, selector.selector)
    if (file.isDirectory) file.listRecursively.toList.sortBy(_.name)
    else if (file.exists) List(file)
    else workingDir.glob(selector.selector).toList.sortBy(_.name)
  }
}
