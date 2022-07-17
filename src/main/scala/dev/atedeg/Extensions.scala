package dev.atedeg

import scala.language.higherKinds
import scala.util.Try

import better.files.File
import cats.Traverse
import cats.syntax.all._

object Extensions {

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit final class AnyOps[A](self: A) {
    def ===(other: A): Boolean = self == other
  }

  implicit class TraversableOps[A, T[_]: Traverse](val s: T[A]) {
    type OrError[B] = Either[Error, B]
    def traverseError[C](f: A => OrError[C]): Either[Error, T[C]] = s.traverse[OrError, C](f)
  }

  implicit class BetterMaybes[A](val s: Set[Option[A]]) {
    def dropNone: Set[A] = s.collect { case Some(a) => a }
  }

  implicit class BetterFile(val file: File) {
    def stringContent: Either[Error, String] = Try(file.contentAsString).toEither.leftMap(ExternalError)
    def parseWith[A](parser: String => Either[Error, A]): Either[Error, A] = file.stringContent.flatMap(parser)
  }
}
