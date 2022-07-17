package dev.atedeg

import scala.util.Try

import better.files.File
import cats.syntax.all._

object Utils {

  private def openFile(file: File): Either[Error, String] =
    Try(file.contentAsString).toEither.leftMap(ExternalError)

  def parseFileWith[A](file: File)(parser: String => Either[Error, A]): Either[Error, A] =
    openFile(file).flatMap(parser)
}
