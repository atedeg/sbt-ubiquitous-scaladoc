package dev.atedeg

import scala.util.Try

import dev.atedeg.HtmlParsing.{ extractClassLike, extractNonClassLike }

import better.files.File
import cats.implicits.*
import net.steppschuh.markdowngenerator.table.Table.ALIGN_LEFT
import net.steppschuh.markdowngenerator.table.Table.Builder

import Extensions.*

final case class Row(term: String, definition: String)

final case class Table(title: String, termName: String, definitionName: String, rows: List[Row]) {

  override def toString: String = {
    val builder = new Builder().withAlignment(ALIGN_LEFT).addRow(termName, definitionName)
    for { Row(term, definition) <- rows } builder.addRow(term, definition)
    builder.build.serialize
  }

  def serialize(targetDir: File): Try[Unit] = {
    val file = targetDir / s"$title.md"
    Try(file.createFileIfNotExists(createParents = true).write(this.toString))
  }
}

sealed trait Error
final case class FileNotFound(lookupDir: File, path: String) extends Error
final case class ParseError(file: File, tag: String) extends Error
final case class AmbiguousName(name: String) extends Error
final case class OverlappingIgnoredAndConsidered(overlapping: Set[IgnoredSelector]) extends Error
final case class LeftoverEntities(leftovers: Set[IgnoredSelector]) extends Error

object Table {

  def parse(config: TableConfig, lookupDir: File): Either[Error, Table] = for {
    rows <- config.rows.traverse(Internals.parseRow(_, lookupDir))
    title = config.name
    termName = config.termName.getOrElse("Term")
    definitionName = config.definitionName.getOrElse("Definition")
  } yield Table(title, termName, definitionName, rows)

  private object Internals {

    def parseRow(row: Selector, lookupDir: File): Either[Error, Row] = row match {
      case Class(name) => parseClassLike(name, lookupDir)
      case Trait(name) => parseClassLike(name, lookupDir)
      case Enum(name) => parseClassLike(name, lookupDir)
      case Type(name, lookupFile) => parseNonClassLike(name, lookupFile, lookupDir)
      case EnumCase(name, lookupFile) => parseNonClassLike(name, lookupFile, lookupDir)
    }

    def parseClassLike(name: String, lookupDir: File): Either[Error, Row] = for {
      file <- findFile(name + ".html", lookupDir)
      termDefinition <- extractClassLike(file)
    } yield Row(termDefinition._1, termDefinition._2)

    def parseNonClassLike(name: String, lookupFile: String, lookupDir: File): Either[Error, Row] = for {
      file <- findFile(lookupFile, lookupDir)
      termDefinition <- extractNonClassLike(file, name)
    } yield Row(termDefinition._1, termDefinition._2)

    def findFile(name: String, lookupDir: File): Either[Error, File] =
      lookupDir.listHtmlFiles.filter(_.name == name).toList match {
        case Nil => FileNotFound(lookupDir, name).asLeft
        case List(f) => f.asRight
        case _ => AmbiguousName(name).asLeft
      }
  }
}
