package dev.atedeg

import better.files.File
import io.circe.{ Decoder, Json }
import io.circe.yaml.parser
import cats.syntax.all._

import Extensions._

object ConfigurationParsing {
  private val yamlConfigFile = ".ubidoc.yaml"
  private val ymlConfigFile = ".ubidoc.yml"

  def readConfiguration(workingDir: File): Either[Error, Configuration] = {
    (workingDir / yamlConfigFile).parseWith(parse) match {
      case res @ Right(_) => res
      case Left(ExternalError(_)) => (workingDir / ymlConfigFile).parseWith(parse)
      case err @ Left(_) => err
    }
  }

  private[atedeg] def parse(raw: String): Either[Error, Configuration] =
    parser.parse(raw).leftMap[Error](CirceParsingFailure).flatMap(parseJson)

  private def parseJson(json: Json): Either[CirceDecodingFailure, Configuration] = {
    import io.circe.generic.auto._
    implicit val decodeEntity: Decoder[BaseEntity] = EntityType.cases
      .map(t => (t.toString, BaseEntity(t, _)))
      .map(entry => Decoder.forProduct1(entry._1)(entry._2))
      .reduceLeft(_ or _)
    implicit val decodeNamedEntity: Decoder[NamedBaseEntity] = EntityType.cases
      .map(t => (t.toString, NamedBaseEntity(t, _, _)))
      .map(entry => Decoder.forProduct2(entry._1, "name")(entry._2))
      .reduceLeft(_ or _)
    json.as[Configuration].leftMap(CirceDecodingFailure)
  }
}

object ConfigurationValidation {

  def toTable(tableConfig: TableConfig, allEntities: Set[Entity]): Either[Error, Table[(Option[String], Entity)]] =
    for {
      entities <- lookupEntities(tableConfig, allEntities)
      termName = tableConfig.termName.getOrElse("Term")
      definitionName = tableConfig.definitionName.getOrElse("Definition")
    } yield Table(tableConfig.name, termName, definitionName, entities)

  @SuppressWarnings(Array("org.wartremover.warts.Any", "org.wartremover.warts.NonUnitStatements"))
  private def lookupEntities(
      config: TableConfig,
      allEntities: Set[Entity],
  ): Either[Error, List[(Option[String], Entity)]] = {
    def matching(baseEntity: NamedBaseEntity)(entity: Entity): Boolean =
      entity.fullyQualifiedName.endsWith(baseEntity.name) &&
        baseEntity.entityType === entity.entityType
    def getEntity(baseEntity: NamedBaseEntity): Either[Error, (Option[String], Entity)] = {
      allEntities.filter(matching(baseEntity)).toList match {
        case Nil => EntityNotFound(baseEntity.toBaseEntity).asLeft[(Option[String], Entity)]
        case List(e) => (baseEntity.wantedName, e).asRight[Error]
        case l => AmbiguousEntityName(baseEntity.name, l).asLeft[(Option[String], Entity)]
      }
    }
    config.rows.traverseError(getEntity)
  }
}
