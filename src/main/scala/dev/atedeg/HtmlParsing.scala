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
      doc <- extractDoc(file, document, entity, allEntities)
    } yield (entity.name, doc)

  def extractDoc(
      file: File,
      document: Browser#DocumentType,
      entity: Entity,
      allEntities: Set[Entity],
  ): Either[Error, String] = {
    val searchQuery = s"${entity.entityId.map("div#" + _ + " ").getOrElse("")}div.cover > div.doc"
    extractTagFromDocument(file, document, searchQuery, allEntities)
  }

  private def extractTagFromDocument(
      file: File,
      doc: Browser#DocumentType,
      tag: String,
      allEntities: Set[Entity],
  ): Either[Error, String] =
    doc.tryExtract(element(tag)).map(_.childNodes).toRight(ParseError(file, tag)).flatMap(toMarkdown(_, allEntities))

  private def toMarkdown(es: Iterable[Node], allEntities: Set[Entity]): Either[Error, String] = {
    def isLink(e: Element): Boolean = e.tagName == "a"
    def toMarkdownLink(e: Element) = lookupLinkFor(extractName(e)).map(l => s"[${e.text}]($l)")
    def extractName(e: Element): String = e.attr("href").replace(".html", "").split('$').last.split("/").last
    def lookupLinkFor(name: String): Either[Error, String] =
      allEntities.find(_.name == name).map("../" + _.link).toRight(MissingLink(name))

    es.foldLeft("".asRight[Error]) { (acc, elem) =>
      elem match {
        case TextNode(s) => acc.map(_ + s)
        case ElementNode(e) if isLink(e) =>
          for {
            a <- acc
            l <- toMarkdownLink(e)
          } yield a + l
        case ElementNode(e) =>
          for {
            a <- acc
            m <- toMarkdown(e.childNodes, allEntities)
          } yield a + m
      }
    }
  }
}
