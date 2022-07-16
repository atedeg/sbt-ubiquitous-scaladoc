package dev.atedeg

import better.files.File
import net.ruippeixotog.scalascraper.browser.{ Browser, JsoupBrowser }
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.dsl.DSL._
import cats.syntax.all._

object HtmlParsing {

  def extractTermAndDefinition(file: File, entity: Entity, allEntities: Set[Entity]): Either[Error, (String, String)] =
    for {
      document <- JsoupBrowser().parseFile(file.toJava).asRight
      doc <- extractDoc(file, document, entity)
    } yield (entity.name, doc)

  def extractDoc(file: File, document: Browser#DocumentType, entity: Entity): Either[Error, String] = {
    val searchQuery = s"#${entity.entityId.map(_ + " > ").getOrElse("")}div.cover > div.doc"
    extractTagFromDocument(file, document, searchQuery)
  }

  private def extractTagFromDocument(file: File, doc: Browser#DocumentType, tag: String): Either[Error, String] =
    doc.tryExtract(element(tag)).map(_.childNodes).map(toMarkdown).toRight(ParseError(file, tag))

  private def toMarkdown(es: Iterable[Node]): String = {
    def isLink(e: Element): Boolean = e.tagName == "a"
    def toMarkdownLink(e: Element): String = s"[${extractName(e.text)}](${e.text})"
    def extractName(fullPath: String): String = fullPath.split('.').last

    es.foldLeft("") { (acc, elem) =>
      elem match {
        case TextNode(s) => acc + s
        case ElementNode(e) if isLink(e) => acc + toMarkdownLink(e)
        case ElementNode(e) => acc + toMarkdown(e.childNodes)
      }
    }
  }
}
