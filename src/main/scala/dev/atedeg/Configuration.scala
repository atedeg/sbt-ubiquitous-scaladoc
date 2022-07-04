package dev.atedeg

import scala.util.Try
import better.files.File
import io.circe.{Decoder, Json}
import io.circe.generic.auto.*
import io.circe.yaml.parser
import cats.implicits.*

final case class Selector(selector: String)
final case class Configuration(ignored: List[Selector], tables: List[TableConfig])
final case class TableConfig(name: String, columns: List[ColumnConfig], rows: List[Selector])
final case class ColumnConfig(name: String, selector: String)

object Configuration {
  private val configFile = ".ubidoc"

  def read(defaultLocation: File): Either[String, Configuration] =
    Try((defaultLocation / configFile).contentAsString).toEither.leftMap(_.toString).flatMap(parse)

  def parse(raw: String): Either[String, Configuration] =
    parser.parse(raw).toOption.flatMap(parseJson).toRight("Can not parse configuration file")

  private def parseJson(json: Json): Option[Configuration] = {
    implicit val decoder: Decoder[List[Selector]] =
      Decoder.decodeList(Decoder.decodeString.map(Selector(_)))
    json.as[Configuration].toOption
  }
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
