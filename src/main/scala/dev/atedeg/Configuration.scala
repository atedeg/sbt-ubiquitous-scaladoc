package dev.atedeg

import scala.util.Try

import better.files.File
import io.circe.Json
import io.circe.generic.auto.*
import io.circe.yaml.parser
import cats.implicits._

final case class Selector(selector: String)
final case class Configuration(ignored: List[Selector], tables: List[TableConfig])
final case class TableConfig(name: String, columns: List[ColumnConfig], rows: List[Selector])
final case class ColumnConfig(name: String, selector: String)

object Configuration {

  def read(defaultLocation: File): Either[String, Configuration] =
    Try((defaultLocation / ".unidoc").contentAsString).toEither.leftMap(_.toString).flatMap(parse)

  private def parse(raw: String): Either[String, Configuration] =
    parser.parse(raw).toOption.flatMap(parseJson).toRight("Can not parse configuration file")
  private def parseJson(json: Json): Option[Configuration] = json.as[Configuration].toOption
}

object Selector {

  def toFiles(workingDir: File, selector: Selector): Either[String, List[File]] = {
    val file = File(workingDir, selector.selector)
    Try(
      if (file.isDirectory) file.listRecursively.toList.sortBy(_.name)
      else if (file.exists) List(file)
      else workingDir.glob(selector.selector).toList.sortBy(_.name),
    ).toEither.leftMap(_.toString)
  }
}
