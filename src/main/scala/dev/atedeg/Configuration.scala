package dev.atedeg

import scala.util.Try

import better.files.File
import io.circe.parser.{ parse => parseJsonString }
import io.circe.{ Decoder, Json }
import io.circe.yaml.parser
import cats.implicits.*

import Extensions.*

sealed trait IgnoredSelector
final case class IgnoredClass(className: String) extends IgnoredSelector
final case class IgnoredTrait(traitName: String) extends IgnoredSelector
final case class IgnoredEnum(enumName: String) extends IgnoredSelector
final case class IgnoredType(typeName: String) extends IgnoredSelector
final case class IgnoredEnumCase(caseName: String) extends IgnoredSelector

object IgnoredSelector {

  def fromKind(name: String, kind: String): Option[IgnoredSelector] = kind match {
    case "class" => Some(IgnoredClass(name))
    case "trait" => Some(IgnoredTrait(name))
    case "type" => Some(IgnoredType(name))
    case "enum" => Some(IgnoredEnum(name))
    case "case" => Some(IgnoredEnumCase(name))
    case _ => None
  }
}

sealed trait Selector {

  def toIgnored: IgnoredSelector = this match {
    case Class(className) => IgnoredClass(className)
    case Trait(traitName) => IgnoredTrait(traitName)
    case Enum(enumName) => IgnoredEnum(enumName)
    case Type(typeName, _) => IgnoredType(typeName)
    case EnumCase(caseName, _) => IgnoredEnumCase(caseName)
  }
}
final case class Class(className: String) extends Selector
final case class Trait(traitName: String) extends Selector
final case class Enum(enumName: String) extends Selector
final case class Type(typeName: String, lookupFile: String) extends Selector
final case class EnumCase(caseName: String, lookupFile: String) extends Selector

final case class Configuration(ignored: Set[IgnoredSelector], tables: List[TableConfig])

final case class TableConfig(
    name: String,
    termName: Option[String],
    definitionName: Option[String],
    rows: List[Selector],
)

private object Utils {

  private def openFile(file: File): Either[Error, String] =
    Try(file.contentAsString).toEither.leftMap(ExternalError)

  def parseFileWith[A](file: File)(parser: String => Either[Error, A]): Either[Error, A] =
    openFile(file).flatMap(parser)
}

object AllEntities {
  private val entitiesFileName = "searchData.js"

  private def allEntitiesFile(workingDir: File): File =
    workingDir / "target" / "site" / "scripts" / entitiesFileName

  def read(workingDir: File): Either[Error, Set[IgnoredSelector]] =
    Utils.parseFileWith(allEntitiesFile(workingDir))(parse)

  private[atedeg] def parse(raw: String): Either[Error, Set[IgnoredSelector]] = {
    val sanitizedRaw = raw.replaceFirst("pages = ", "").replaceFirst(";", "")
    parseJsonString(sanitizedRaw).leftMap(CirceParsingFailure).flatMap(parseJson)
  }

  private def parseJson(json: Json): Either[Error, Set[IgnoredSelector]] = {
    implicit val decodeIgnoredSelector: Decoder[Option[IgnoredSelector]] =
      Decoder.forProduct2("n", "k")(IgnoredSelector.fromKind)
    json.as[Set[Option[IgnoredSelector]]].map(_.dropNone).leftMap(CirceDecodingFailure)
  }
}

object Configuration {
  private val configFile = ".ubidoc"

  def read(workingDir: File): Either[Error, Configuration] =
    Utils.parseFileWith(workingDir / configFile)(parse)

  private[atedeg] def parse(raw: String): Either[Error, Configuration] =
    parser.parse(raw).leftMap[Error](CirceParsingFailure).flatMap(parseJson)

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  private def parseJson(json: Json): Either[CirceDecodingFailure, Configuration] = {
    import io.circe.generic.auto.*
    implicit val decodeSelector: Decoder[Selector] = {
      List[Decoder[Selector]](
        Decoder[Class].widen,
        Decoder[Trait].widen,
        Decoder[Enum].widen,
        Decoder[Type].widen,
        Decoder[EnumCase].widen,
      ).reduceLeft(_ or _)
    }
    implicit val decodeIgnoredSelector: Decoder[IgnoredSelector] = {
      List[Decoder[IgnoredSelector]](
        Decoder[IgnoredClass].widen,
        Decoder[IgnoredTrait].widen,
        Decoder[IgnoredEnum].widen,
        Decoder[IgnoredType].widen,
        Decoder[IgnoredEnumCase].widen,
      ).reduceLeft(_ or _)
    }
    json.as[Configuration].leftMap(CirceDecodingFailure)
  }
}
