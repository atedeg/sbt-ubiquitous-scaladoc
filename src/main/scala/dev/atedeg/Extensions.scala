package dev.atedeg

import scala.language.implicitConversions

import better.files.File
import cats.implicits.*

object Extensions {
  implicit def eitherToEitherString[A, E](e: Either[E, A]): Either[String, A] = e.leftMap(_.toString)

  implicit class BetterFileIterator(val i: Iterator[File]) {
    def keepHtmlFiles: Iterator[File] = i.filter(_.isHtmlFile)
  }

  implicit class BetterFile(val f: File) {
    def isHtmlFile: Boolean = f.extension.contains(".html")
    def listHtmlFiles: Iterator[File] = f.listRecursively.keepHtmlFiles
    def globHtmlFiles(g: String): Iterator[File] = f.glob(g).keepHtmlFiles
  }
}
