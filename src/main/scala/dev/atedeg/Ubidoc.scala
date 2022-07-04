package dev.atedeg

import java.io.{ File => JFile }

import scala.util.Try

import better.files.{ File, FileExtensions }
import cats.implicits.*

import Extensions._

object Ubidoc {

  def apply(workingDir: JFile, targetDir: JFile, baseDir: JFile): Unit =
    Internals.ubiquitousScaladocTask(workingDir.toScala, targetDir.toScala, baseDir.toScala)

  private object Internals {

    def ubiquitousScaladocTask(workingDir: File, targetDir: File, baseDir: File): Unit = {
      val leftoverFiles = for {
        conf <- Configuration.read(baseDir)
        allFiles <- listAllFiles(workingDir)
        ignoredFiles <- getIgnoredFiles(conf, workingDir)
        parsedFiles <- parseAllFiles(conf, workingDir, targetDir, ignoredFiles)
      } yield allFiles -- ignoredFiles -- parsedFiles

      leftoverFiles match {
        case Left(err) => throw new IllegalStateException(err)
        case Right(s) if s.isEmpty => println("Done!")
        case Right(s) => throw new IllegalStateException(s"Unparsed files: $s")
      }
    }

    def listAllFiles(workingDir: File): Either[String, Set[File]] =
      Try(workingDir.listHtmlFiles).toEither.map(_.toSet)

    def getIgnoredFiles(conf: Configuration, workingDir: File): Either[String, Set[File]] =
      conf.ignored.flatTraverse(_.toFiles(workingDir)).map(_.toSet)

    def parseAllFiles(
        conf: Configuration,
        workingDir: File,
        targetDir: File,
        ignoredFiles: Set[File],
    ): Either[String, Set[File]] =
      for {
        res <- conf.tables.traverse(Table.parse(workingDir, _, ignoredFiles))
        tables = res.map(_._1)
        _ <- tables.traverse(_.serialize(targetDir)).toEither
        files = res.flatMap(_._2)
      } yield files.toSet

  }
}
