package dev.atedeg

import scala.util.Try

import better.files.File
import io.circe.{ Decoder, Json }
import io.circe.generic.auto._
import io.circe.yaml.parser
import cats.implicits._

import Extensions._

final case class Selector(selector: String) {

  def toFiles(workingDir: File): Either[String, List[File]] = {
    Try(unsafeToFiles(workingDir))
      .map(_.sortBy(_.name))
      .toEither
      .filterOrElse(_.nonEmpty, s"No files found matching selector $selector")
  }

  private def unsafeToFiles(workingDir: File): List[File] =
    htmlFilesInDirectory(File(workingDir, selector)) ++ selectorToFileOrGlob(workingDir)

  private def selectorToFileOrGlob(workingDir: File): List[File] =
    workingDir.listHtmlFiles.find(_.nameWithoutExtension == selector) match {
      case Some(f) => List(f)
      case None => workingDir.globHtmlFiles(selector).toList
    }

  private def htmlFilesInDirectory(dir: File): List[File] =
    if (dir.isDirectory) dir.listHtmlFiles.toList else List()
}

final case class Configuration(ignored: List[Selector], tables: List[TableConfig])
final case class TableConfig(name: String, columns: List[ColumnConfig], rows: List[Selector])
final case class ColumnConfig(name: String, selector: String)

object Configuration {
  private val configFile = ".ubidoc"

  def read(defaultLocation: File): Either[String, Configuration] =
    Try((defaultLocation / configFile).contentAsString).toEither.flatMap(parse)

  def parse(raw: String): Either[String, Configuration] = parser.parse(raw).flatMap(parseJson)

  private def parseJson(json: Json): Either[String, Configuration] = {
    implicit val decoder: Decoder[List[Selector]] = Decoder.decodeList(Decoder.decodeString.map(Selector))
    json.as[Configuration]
  }
}
