package dev.atedeg

import scala.util.Try

import dev.atedeg.EntityConversion.entityToRow

import better.files.File
import net.steppschuh.markdowngenerator.table.Table.{ ALIGN_LEFT, Builder }

import Extensions._

object TableUtils {

  def entitiesToRows(
      table: Table[(Option[String], Entity)],
      baseDir: File,
      allEntities: Set[Entity],
  ): Either[Error, Table[Row]] =
    table.rows
      .traverseError(entityToRow(_, baseDir, allEntities))
      .map(Table(table.title, table.termName, table.definitionName, _))

  def serialize(table: Table[Row], targetDir: File): Try[Unit] = {
    val file = targetDir / s"${table.title}.md"
    Try(file.createFileIfNotExists(createParents = true).write(show(table)))
  }

  private def show(table: Table[Row]): String = {
    val builder = new Builder().withAlignment(ALIGN_LEFT).addRow(table.termName, table.definitionName)
    for { Row(term, definition) <- table.rows } builder.addRow(term, definition)
    builder.build.serialize
  }
}
