package dev.atedeg

import java.io.{File => JFile}
import better.files.{File, FileExtensions}
import ConfigurationValidation._
import cats.implicits._
import dev.atedeg.ConfigurationParsing.readConfiguration
import dev.atedeg.EntityParsing.readAllEntities
import dev.atedeg.TableUtils.entitiesToRows

object Ubidoc {

  def apply(lookupDir: JFile, targetDir: JFile, workingDir: JFile): Unit =
    Internals.ubiquitousScaladocTask(lookupDir.toScala, targetDir.toScala, workingDir.toScala)

  private object Internals {

    def ubiquitousScaladocTask(lookupDir: File, targetDir: File, workingDir: File): Unit = {
      val result = for {
        config <- readConfiguration(workingDir)
        allEntities <- readAllEntities(workingDir)
        tables <- config.tables.traverse(toTable(_, allEntities))
        consideredEntities = tables.flatMap(_.rows).toSet
        _ <- checkConsistency(allEntities.map(_.toBaseEntity), consideredEntities.map(_.toBaseEntity), config.ignored)
        tables <- tables.traverse(entitiesToRows(_, lookupDir, allEntities))
      } yield tables.foreach(_.serialize(targetDir))
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
        OverlappingIgnoredAndConsidered(consideredAndIgnoredIntersection).asLeft
      else if (leftoverEntities.nonEmpty) LeftoverEntities(leftoverEntities).asLeft
      else ().asRight
    }

  }
}
