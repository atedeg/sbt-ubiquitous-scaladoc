package dev.atedeg

import better.files.File

object Extensions {

  implicit class BetterFile(val f: File) {
    def isHtmlFile: Boolean = f.extension.contains(".html")
    def listHtmlFiles: Iterator[File] = f.listRecursively.filter(_.isHtmlFile)
  }
}
