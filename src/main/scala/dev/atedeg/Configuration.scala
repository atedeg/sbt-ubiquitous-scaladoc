package dev.atedeg

import scala.util.Try
import better.files.File
import io.circe.{Decoder, Json}
import io.circe.generic.auto.*
import io.circe.yaml.parser
import cats.implicits.*
import Extensions.*

sealed trait Selector {
  def toFiles(workingDir: File): Either[String, List[File]] =
    Try(unsafeToFiles(workingDir, this))
      .map(_.sortBy(_.name))
      .toEither
      .filterOrElse(_.nonEmpty, s"No files found matching selector $this")

  private def unsafeToFiles(workingDir: File, s: Selector): List[File] = s match {
    case FileSelector(file) => workingDir.listHtmlFiles.find(_.nameWithoutExtension == file).toList
    case DirSelector(dir) => htmlFilesInDirectory(File(workingDir, dir))
    case GlobSelector(glob) => workingDir.globHtmlFiles(glob).toList
  }

  private def htmlFilesInDirectory(dir: File): List[File] =
    if (dir.isDirectory) dir.listHtmlFiles.toList else List()
}
final case class FileSelector(file: String) extends Selector
final case class DirSelector(dir: String) extends Selector
final case class GlobSelector(glob: String) extends Selector

final case class Configuration(ignored: List[Selector], tables: List[TableConfig])
final case class TableConfig(name: String, columns: List[ColumnConfig], rows: List[Selector])
final case class ColumnConfig(name: String, htmlTag: String)

object Configuration {
  private val configFile = ".ubidoc"

  def read(defaultLocation: File): Either[String, Configuration] =
    Try((defaultLocation / configFile).contentAsString).toEither.flatMap(parse)

  def parse(raw: String): Either[String, Configuration] = parser.parse(raw).flatMap(parseJson)

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  private def parseJson(json: Json): Either[String, Configuration] = {
    implicit val decodeSelector: Decoder[Selector] = {
      List[Decoder[Selector]](
        Decoder[FileSelector].widen,
        Decoder[DirSelector].widen,
        Decoder[GlobSelector].widen,
      ).reduceLeft(_ or _)
    }
    json.as[Configuration]
  }
}
