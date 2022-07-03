package dev.atedeg

import io.circe.Json
import io.circe.generic.auto._
import io.circe.yaml.parser

final case class Selector(selector: String)
final case class Configuration(ignored: List[Selector], tables: List[TableConfig])
final case class TableConfig(name: String, columns: List[ColumnConfig], rows: List[Selector])
final case class ColumnConfig(name: String, selector: String)

object ParseConfiguration {
  def parseConfiguration(raw: String): Option[Configuration] = parser.parse(raw).toOption.flatMap(parseJson)
  private def parseJson(json: Json): Option[Configuration] = json.as[Configuration].toOption
}
