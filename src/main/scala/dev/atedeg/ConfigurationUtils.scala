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
      allEntities.find(_.toBaseEntity === baseEntity).toRight(EntityNotFound(baseEntity))
    config.rows.traverseError(lookup)
  }
}
