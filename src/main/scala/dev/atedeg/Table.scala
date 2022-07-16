package dev.atedeg

import java.util.Locale.US

import scala.util.Try
import better.files.File
import net.steppschuh.markdowngenerator.table.Table.ALIGN_LEFT
import net.steppschuh.markdowngenerator.table.Table.Builder

final case class Table[E](title: String, termName: String, definitionName: String, rows: List[E]) {

  def map[A](f: E => A): Table[A] = Table(title, termName, definitionName, rows.map(f))

  override def toString: String = {
    val builder = new Builder().withAlignment(ALIGN_LEFT).addRow(termName, definitionName)
    for { Row(term, definition) <- rows } builder.addRow(term, definition)
    builder.build.serialize
  }

  def serialize(targetDir: File): Try[Unit] = {
    val file = targetDir / s"$title.md"
    Try(file.createFileIfNotExists(createParents = true).write(this.toString))
  }
}

final case class Row private (term: String, definition: String)

object Row {

  private def normalizeName(name: String): String =
    name.split("(?=\\p{Upper})").toList.map(_.toLowerCase(US)).mkString(" ").capitalize

  def apply(termDefinition: (String, String)): Row = Row(normalizeName(termDefinition._1), termDefinition._2)
  def apply(term: String, definition: String): Row = Row((normalizeName(term), definition))
}
