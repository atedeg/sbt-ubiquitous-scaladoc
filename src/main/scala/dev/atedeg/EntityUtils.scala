package dev.atedeg

import java.util.Locale.US

import dev.atedeg.HtmlParsing.extractTermAndDefinition

import better.files.File
import io.circe.{ Decoder, Json }
import cats.syntax.all._
import io.circe.parser.{ parse => parseJsonString }

import Extensions._

object EntityParsing {
  private val entitiesFileName = "searchData.js"

  def readAllEntities(lookupDir: File): Either[Error, Set[Entity]] =
    (lookupDir / "scripts" / entitiesFileName).parseWith(parse)

  private[atedeg] def parse(raw: String): Either[Error, Set[Entity]] = {
    val sanitized = raw.replaceFirst("pages = ", "").replaceFirst(";", "")
    parseJsonString(sanitized).leftMap(CirceParsingFailure).flatMap(parseJson)
  }

  private def parseJson(json: Json): Either[Error, Set[Entity]] = {
    def build(maybeType: Option[EntityType], link: String, name: String, packageName: String): Option[Entity] =
      maybeType.map(Entity(_, link, name, packageName))

    implicit val decodeEntityType: Decoder[Option[EntityType]] = Decoder.decodeString.map(EntityType.read)
    implicit val decodeEntity: Decoder[Option[Entity]] = Decoder.forProduct4("k", "l", "n", "d")(build)
    json.as[Set[Option[Entity]]].map(_.dropNone).leftMap(CirceDecodingFailure)
  }
}

object EntityConversion {

  private def normalizeName(name: String): String =
    name.split("(?=\\p{Upper})").toList.map(_.toLowerCase(US)).mkString(" ").capitalize

  def entityToRow(
      namedEntity: (Option[String], Entity),
      baseDir: File,
      allEntities: Set[Entity],
      linkSolver: String => String,
  ): Either[Error, Row] =
    extractTermAndDefinition(baseDir / namedEntity._2.sanitizedLink, namedEntity._2, allEntities, linkSolver).map {
      case (term, definition) => Row(namedEntity._1.getOrElse(normalizeName(term)), definition)
    }
}
