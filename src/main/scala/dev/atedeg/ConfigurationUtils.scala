package dev.atedeg

import better.files.File
import cats.data.NonEmptyList
import io.circe.{Decoder, Json}
import io.circe.yaml.parser
import cats.syntax.all._

object ConfigurationParsing {
  private val configFile = ".ubidoc.yaml"

  def readConfiguration(workingDir: File): Either[Error, Configuration] = Utils.parseFileWith(workingDir / configFile)(parse)

  private[atedeg] def parse(raw: String): Either[Error, Configuration] =
    parser.parse(raw).leftMap[Error](CirceParsingFailure).flatMap(parseJson)

  private def parseJson(json: Json): Either[CirceDecodingFailure, Configuration] = {
    import io.circe.generic.auto._
    implicit val decodeEntity: Decoder[BaseEntity] = NonEmptyList.of[EntityType](Class, Trait, Enum, Case, Type, Def)
      .map(t => (t.toString, BaseEntity(t, _)))
      .map(entry => Decoder.forProduct1(entry._1)(entry._2))
      .reduceLeft(_ or _)
    json.as[Configuration].leftMap(CirceDecodingFailure)
  }
}

object ConfigurationValidation {
  def toTable(tableConfig: TableConfig, allEntities: Set[Entity]): Either[Error, Table[Entity]] = for {
    entities <- lookupEntities(tableConfig, allEntities)
    termName = tableConfig.termName.getOrElse("Term")
    definitionName = tableConfig.definitionName.getOrElse("Definition")
  } yield Table(tableConfig.name, termName, definitionName, entities)

  private def lookupEntities(config: TableConfig, allEntities: Set[Entity]): Either[Error, List[Entity]] = {
    def lookup(baseEntity: BaseEntity): Either[Error, Entity] =
      allEntities.find(_.toBaseEntity == baseEntity).toRight(EntityNotFound(baseEntity))
    config.rows.traverse(lookup)
  }
}
