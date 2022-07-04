package dev.atedeg

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.*
import net.ruippeixotog.scalascraper.model.*
import net.ruippeixotog.scalascraper.dsl.DSL.*

object HtmlParsing {

  def extractColumn(
      document: Browser#DocumentType,
      fileName: String,
      columnConfig: ColumnConfig,
  ): Either[String, String] = {
    (document >?> element(columnConfig.htmlTag))
      .map(_.childNodes)
      .map(toMarkdown(_, fileName))
      .toRight(s"Cannot extract column ${columnConfig.name}")
  }

  private def toMarkdown(es: Iterable[Node], fileName: String): String = {
    es.foldLeft("") { (acc, elem) =>
      elem match {
        case TextNode(s) => acc + s
        case ElementNode(e) if isLink(e) => acc + toMarkdownLink(e, fileName)
        case ElementNode(e) => acc + toMarkdown(e.childNodes, fileName)
      }
    }
  }

  private def isLink(e: Element): Boolean = e.tagName == "a"
  private def toMarkdownLink(e: Element, fileName: String): String = s"[$fileName](${e.text})"

}
