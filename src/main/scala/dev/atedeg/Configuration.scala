package dev.atedeg

import scala.util.Try

import better.files.File
import io.circe.parser.{ parse => parseJsonString }
import io.circe.{ Decoder, Json }
import io.circe.yaml.parser
import cats.implicits._
import Extensions._

sealed trait EntityType {
  override def toString: String = this match {
    case Class => "class"
    case Trait => "trait"
    case Enum => "enum"
    case Type => "type"
    case Case => "case"
    case Def => "def"
  }
}

object EntityType {

  def fromString(s: String): Option[EntityType] = s match {
    case "class" => Some(Class)
    case "trait" => Some(Trait)
    case "enum" => Some(Enum)
    case "type" => Some(Type)
    case "case" => Some(Case)
    case "def" => Some(Def)
    case _ => None
  }
}
case object Class extends EntityType
case object Trait extends EntityType
case object Enum extends EntityType
case object Type extends EntityType
case object Case extends EntityType
case object Def extends EntityType

final case class Entity(entityType: EntityType, link: String, name: String, packageName: String)
final case class BaseEntity(entityType: EntityType, name: String)

object Entity {
  private val entitiesFileName = "searchData.js"

  private def allEntitiesFile(workingDir: File): File =
    workingDir / "target" / "site" / "scripts" / entitiesFileName

  def readAll(workingDir: File): Either[Error, Set[Entity]] = Utils.parseFileWith(allEntitiesFile(workingDir))(parse)

  private[atedeg] def parse(raw: String): Either[Error, Set[Entity]] =
    parseJsonString(raw).leftMap(CirceParsingFailure).flatMap(parseJson)

  private def parseJson(json: Json): Either[Error, Set[Entity]] = {
    def build(maybeType: Option[EntityType], link: String, name: String, packageName: String): Option[Entity] =
      maybeType.map(Entity(_, link, name, packageName))

    implicit val decodeEntityType: Decoder[Option[EntityType]] = Decoder.decodeString.map(EntityType.fromString)
    implicit val decodeEntity: Decoder[Option[Entity]] = Decoder.forProduct4("k", "l", "n", "d")(build)
    json.as[Set[Option[Entity]]].map(_.dropNone).leftMap(CirceDecodingFailure)
  }
}

final case class Configuration(ignored: Set[BaseEntity], tables: List[TableConfig])
final case class TableConfig(
    name: String,
    termName: Option[String],
    definitionName: Option[String],
    rows: List[BaseEntity],
)

object Configuration {
  private val configFile = ".ubidoc.yaml"

  def read(workingDir: File): Either[Error, Configuration] = Utils.parseFileWith(workingDir / configFile)(parse)

  private[atedeg] def parse(raw: String): Either[Error, Configuration] =
    parser.parse(raw).leftMap[Error](CirceParsingFailure).flatMap(parseJson)

  private def parseJson(json: Json): Either[CirceDecodingFailure, Configuration] = {
    import io.circe.generic.auto._
    implicit val decodeEntity: Decoder[BaseEntity] = List(Class, Trait, Enum, Case, Type, Def)
      .map(t => (t.toString, BaseEntity(t, _)))
      .map(entry => Decoder.forProduct1(entry._1)(entry._2))
      .reduceLeft(_ or _)
    json.as[Configuration].leftMap(CirceDecodingFailure)
  }
}

private object Utils {

  private def openFile(file: File): Either[Error, String] =
    Try(file.contentAsString).toEither.leftMap(ExternalError)

  def parseFileWith[A](file: File)(parser: String => Either[Error, A]): Either[Error, A] =
    openFile(file).flatMap(parser)
}
