package dev.atedeg

import dev.atedeg.EntityConversion.entityToRow
import better.files.File
import Extensions._

object TableUtils {

  def entitiesToRows(table: Table[Entity], baseDir: File, allEntities: Set[Entity]): Either[Error, Table[Row]] =
    table.rows
      .traverseError(entityToRow(_, baseDir, allEntities))
      .map(Table(table.title, table.termName, table.definitionName, _))
}
