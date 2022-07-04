package dev.atedeg

import scala.util.Try
import better.files.File
import io.circe.{Decoder, Json}
import io.circe.generic.auto.*
import io.circe.yaml.parser
import cats.implicits.*

final case class Selector(selector: String) {
  def toFiles(workingDir: File): Either[String, List[File]] = {
    val file = File(workingDir, selector)
    Try(
      if (file.isDirectory) file.listRecursively.toList.sortBy(_.name)
      else if (file.exists) List(file)
      else workingDir.glob(selector).toList.sortBy(_.name),
    ).toEither.leftMap(_.toString)
  }
}

final case class Configuration(ignored: List[Selector], tables: List[TableConfig])
final case class TableConfig(name: String, columns: List[ColumnConfig], rows: List[Selector])
final case class ColumnConfig(name: String, selector: String)

object Configuration {
  private val configFile = ".ubidoc"

  def read(defaultLocation: File): Either[String, Configuration] =
    Try((defaultLocation / configFile).contentAsString).toEither.leftMap(_.toString).flatMap(parse)

  def parse(raw: String): Either[String, Configuration] = parser.parse(raw).leftMap(_.toString).flatMap(parseJson)

  private def parseJson(json: Json): Either[String, Configuration] = {
    implicit val decoder: Decoder[List[Selector]] = Decoder.decodeList(Decoder.decodeString.map(Selector))
    json.as[Configuration].leftMap(_.toString)
  }
}