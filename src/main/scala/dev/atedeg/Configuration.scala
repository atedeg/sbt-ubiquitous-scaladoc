package dev.atedeg

import scala.util.Try

import better.files.File
import io.circe.{ Decoder, Json }
import io.circe.generic.auto.*
import io.circe.yaml.parser
import cats.implicits.*

import Extensions.*

sealed trait IgnoredSelector
final case class IgnoredClass(className: String) extends IgnoredSelector
final case class IgnoredTrait(traitName: String) extends IgnoredSelector
final case class IgnoredEnum(enumName: String) extends IgnoredSelector
final case class IgnoredType(typeName: String) extends IgnoredSelector
final case class IgnoredEnumCase(caseName: String) extends IgnoredSelector

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

object Configuration {
  private val configFile = ".ubidoc"

  def readAllEntities: Either[Error, Set[IgnoredSelector]] = ???

  def read(workingDir: File): Either[String, Configuration] =
    Try((workingDir / configFile).contentAsString).toEither.flatMap(parse)

  def parse(raw: String): Either[String, Configuration] = parser.parse(raw).flatMap(parseJson)

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  private def parseJson(json: Json): Either[String, Configuration] = {
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
    json.as[Configuration]
  }
}
