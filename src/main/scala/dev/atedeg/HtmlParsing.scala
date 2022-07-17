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
      document <- JsoupBrowser().parseFile(file.toJava).asRight[Error]
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

  private def toMarkdown(elems: Iterable[Node], allEntities: Set[Entity]): Either[Error, String] = {
    def isLink(elem: Element) = elem.tagName === "a"
    def toMarkdownLink(elem: Element) = lookupLinkFor(extractName(elem)).map(l => s"[${elem.text}]($l)")
    def lookupLinkFor(name: String) = allEntities.find(_.name === name).map("../" + _.link).toRight(MissingLink(name))
    def extractName(elem: Element) = elem.attr("href").replace(".html", "").split('$').last.split("/").last
    elems.foldLeft("".asRight[Error]) { (acc, elem) =>
      elem match {
        case TextNode(s) => acc.map(_ + s)
        case ElementNode(e) if isLink(e) => acc.flatMap(a => toMarkdownLink(e).map(a + _))
        case ElementNode(e) => acc.flatMap(a => toMarkdown(e.childNodes, allEntities).map(a + _))
      }
    }
  }
}
