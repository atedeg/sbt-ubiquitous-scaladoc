package dev.atedeg

import better.files.File

import scala.util.Try
import cats.syntax.all._

object Utils {
  private def openFile(file: File): Either[Error, String] =
    Try(file.contentAsString).toEither.leftMap(ExternalError)

  def parseFileWith[A](file: File)(parser: String => Either[Error, A]): Either[Error, A] =
    openFile(file).flatMap(parser)
}
