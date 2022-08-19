package dev.atedeg

import java.util.Locale.US

final case class Table[E](title: String, termName: String, definitionName: String, rows: List[E])
final case class Row(term: String, definition: String)
