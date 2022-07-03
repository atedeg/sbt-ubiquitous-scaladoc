package dev.atedeg

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.*
import net.ruippeixotog.scalascraper.model.*
import net.ruippeixotog.scalascraper.dsl.DSL.*

object HtmlParsing {

  def extractColumn(document: Browser#DocumentType, columnConfig: ColumnConfig): Either[String, String] =
    (document >?> element(columnConfig.selector))
      .map(_.childNodes)
      .map(toMarkdown)
      .toRight(s"Cannot extract column ${columnConfig.name}")

  private def toMarkdown(es: Iterable[Node]): String = {
    es.foldLeft("") { (acc, elem) =>
      elem match {
        case TextNode(s) => acc + s
        case ElementNode(e) if isLink(e) => acc + toMarkdownLink(e)
        case ElementNode(e) => acc + toMarkdown(e.childNodes)
      }
    }
  }

  private def isLink(e: Element): Boolean = e.tagName == "a"
  private def toMarkdownLink(e: Element): String = s"[${e.text}](${e.attr("href")})"

}
