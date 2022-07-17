package dev.atedeg

import java.util.Locale.US

final case class Table[E](title: String, termName: String, definitionName: String, rows: List[E])
final case class Row private (term: String, definition: String)

object Row {

  private def normalizeName(name: String): String =
    name.split("(?=\\p{Upper})").toList.map(_.toLowerCase(US)).mkString(" ").capitalize

  def from(termDefinition: (String, String)): Row = Row(normalizeName(termDefinition._1), termDefinition._2)
}
