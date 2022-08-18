package dev.atedeg

import java.io.{ File => JFile }

import dev.atedeg.ConfigurationParsing.readConfiguration
import dev.atedeg.EntityParsing.readAllEntities
import dev.atedeg.TableUtils.{ entitiesToRows, serialize }

import better.files.{ File, FileExtensions }
import cats.implicits._
import sbt.internal.util.ManagedLogger

import Extensions._
import ConfigurationValidation._

object Ubidoc {

  def apply(
      lookupDir: JFile,
      targetDir: JFile,
      workingDir: JFile,
      linkSolver: String => String,
      logger: ManagedLogger,
  ): Unit =
    Internals.ubiquitousScaladocTask(lookupDir.toScala, targetDir.toScala, workingDir.toScala, linkSolver, logger)

  private object Internals {

    def ubiquitousScaladocTask(
        lookupDir: File,
        targetDir: File,
        workingDir: File,
        linkSolver: String => String,
        logger: ManagedLogger,
    ): Unit = {
      val result = for {
        config <- readConfiguration(workingDir)
        allEntities <- readAllEntities(lookupDir)
        tables <- config.tables.traverseError(toTable(_, allEntities))
        consideredEntities = tables.flatMap(_.rows).toSet
        _ <- checkConsistency(
          allEntities.map(_.toBaseEntity),
          consideredEntities.map(_._2.toBaseEntity),
          config.ignored,
          logger,
        )
        tables <- tables.traverseError(entitiesToRows(_, lookupDir, allEntities, linkSolver))
      } yield tables.foreach(serialize(_, targetDir))
      result match {
        case Left(err) => throw UbidocException(err)
        case Right(()) => logger.success("Tables generated")
      }
    }

    private def checkConsistency(
        allEntities: Set[BaseEntity],
        considered: Set[BaseEntity],
        ignored: Set[BaseEntity],
        logger: ManagedLogger,
    ): Either[Error, Unit] = {
      val consideredAndIgnored = considered.intersect(ignored)
      val leftovers = allEntities.diff(considered).diff(ignored)
      if (consideredAndIgnored.nonEmpty)
        OverlappingIgnoredAndConsidered(consideredAndIgnored).asLeft[Unit]
      else if (leftovers.nonEmpty) {
        logger.warn(LeftoverEntities(leftovers).toString)
        ().asRight[Error]
      } else ().asRight[Error]
    }

  }
}
