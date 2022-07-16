package dev.atedeg

import dev.atedeg.EntityConversion.entityToRow

import better.files.File
import cats.syntax.all._

object TableUtils {

  def entitiesToRows(table: Table[Entity], baseDir: File, allEntities: Set[Entity]): Either[Error, Table[Row]] =
    table.rows
      .traverse(entityToRow(_, baseDir, allEntities))
      .map(Table(table.title, table.termName, table.definitionName, _))
}
