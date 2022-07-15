package dev.atedeg

import java.io.{ File => JFile }

import better.files.{ File, FileExtensions }
import cats.implicits.*

object Ubidoc {

  def apply(lookupDir: JFile, targetDir: JFile, workingDir: JFile): Unit =
    Internals.ubiquitousScaladocTask(lookupDir.toScala, targetDir.toScala, workingDir.toScala)

  private object Internals {

    def ubiquitousScaladocTask(lookupDir: File, targetDir: File, workingDir: File): Unit = {
      val result = for {
        config <- Configuration.read(workingDir)
        allEntities <- AllEntities.read(workingDir)
        consideredEntities = config.tables.flatMap(_.rows).toSet
        _ <- checkConsistency(allEntities, consideredEntities, config.ignored)
        tables <- config.tables.traverse(Table.parse(_, lookupDir))
        _ = tables.foreach(_.serialize(targetDir))
      } yield ()
      result match {
        case Left(err) => throw UbidocException(err)
        case Right(()) => ()
      }
    }

    private def checkConsistency(
        allEntities: Set[IgnoredSelector],
        considered: Set[Selector],
        ignored: Set[IgnoredSelector],
    ): Either[Error, Unit] = {
      val c = considered.map(_.toIgnored)
      val consideredAndIgnoredIntersection = c.intersect(ignored)
      val leftoverEntities = allEntities.diff(c).diff(ignored)
      if (consideredAndIgnoredIntersection.nonEmpty)
        OverlappingIgnoredAndConsidered(consideredAndIgnoredIntersection).asLeft
      else if (leftoverEntities.nonEmpty) LeftoverEntities(leftoverEntities).asLeft
      else ().asRight
    }

  }
}
