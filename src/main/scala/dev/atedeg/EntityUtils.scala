package dev.atedeg

import better.files.File
import io.circe.{Decoder, Json}
import cats.syntax.all._
import io.circe.parser.{parse => parseJsonString}
import Extensions._
import dev.atedeg.HtmlParsing.extractTermAndDefinition

object EntityParsing {
  private val entitiesFileName = "searchData.js"

  private def allEntitiesFile(workingDir: File): File =
    workingDir / "target" / "site" / "scripts" / entitiesFileName

  def readAllEntities(workingDir: File): Either[Error, Set[Entity]] = Utils.parseFileWith(allEntitiesFile(workingDir))(parse)

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

object EntityConversion {
  def entityToRow(entity: Entity, baseDir: File, allEntities: Set[Entity]): Either[Error, Row] =
    extractTermAndDefinition(baseDir / entity.sanitizedLink, entity, allEntities).map(Row(_))
}
