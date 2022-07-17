package dev.atedeg

import java.io.{ File => JFile }

import dev.atedeg.ConfigurationParsing.readConfiguration
import dev.atedeg.EntityParsing.readAllEntities
import dev.atedeg.TableUtils.{ entitiesToRows, serialize }

import better.files.{ File, FileExtensions }
import cats.implicits._

import Extensions._
import ConfigurationValidation._

object Ubidoc {

  def apply(lookupDir: JFile, targetDir: JFile, workingDir: JFile): Unit =
    Internals.ubiquitousScaladocTask(lookupDir.toScala, targetDir.toScala, workingDir.toScala)

  private object Internals {

    def ubiquitousScaladocTask(lookupDir: File, targetDir: File, workingDir: File): Unit = {
      val result = for {
        config <- readConfiguration(workingDir)
        allEntities <- readAllEntities(workingDir)
        tables <- config.tables.traverseError(toTable(_, allEntities))
        consideredEntities = tables.flatMap(_.rows).toSet
        _ <- checkConsistency(allEntities.map(_.toBaseEntity), consideredEntities.map(_.toBaseEntity), config.ignored)
        tables <- tables.traverseError(entitiesToRows(_, lookupDir, allEntities))
      } yield tables.foreach(serialize(_, targetDir))
      result match {
        case Left(err) => throw UbidocException(err)
        case Right(()) => println("Tables generated!")
      }
    }

    private def checkConsistency(
        allEntities: Set[BaseEntity],
        considered: Set[BaseEntity],
        ignored: Set[BaseEntity],
    ): Either[Error, Unit] = {
      val consideredAndIgnoredIntersection = considered.intersect(ignored)
      val leftoverEntities = allEntities.diff(considered).diff(ignored)
      if (consideredAndIgnoredIntersection.nonEmpty)
        OverlappingIgnoredAndConsidered(consideredAndIgnoredIntersection).asLeft[Unit]
      else if (leftoverEntities.nonEmpty) LeftoverEntities(leftoverEntities).asLeft[Unit]
      else ().asRight[Error]
    }

  }
}
