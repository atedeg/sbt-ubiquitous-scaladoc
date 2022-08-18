package dev.atedeg

import better.files.File
import net.ruippeixotog.scalascraper.browser.{ Browser, JsoupBrowser }
import net.ruippeixotog.scalascraper.scraper.ContentExtractors._
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.dsl.DSL._
import cats.syntax.all._

object HtmlParsing {

  def extractTermAndDefinition(
      file: File,
      entity: Entity,
      allEntities: Set[Entity],
      linkSolver: String => String,
  ): Either[Error, (String, String)] = {
    val document = JsoupBrowser().parseFile(file.toJava)
    val docQuery = s"${entity.entityId.map("div#" + _ + " ").getOrElse("")}div.cover > div.doc"
    extractTag(file, document, docQuery, allEntities, linkSolver).map((entity.name, _))
  }

  private def extractTag(
      file: File,
      doc: Browser#DocumentType,
      tag: String,
      all: Set[Entity],
      linkSolver: String => String,
  ): Either[Error, String] =
    doc
      .tryExtract(element(tag))
      .map(_.childNodes)
      .toRight(ParseError(file, tag))
      .flatMap(toMarkdown(_, all, linkSolver))

  private def toMarkdown(
      elems: Iterable[Node],
      allEntities: Set[Entity],
      linkSolver: String => String,
  ): Either[Error, String] = {
    def isLink(elem: Element) = elem.tagName === "a"
    def toMarkdownLink(elem: Element) = lookupLinkFor(extractName(elem)).map(linkSolver).map(l => s"[${elem.text}]($l)")
    def lookupLinkFor(name: String) = allEntities.find(_.name === name).map(_.link).toRight(MissingLink(name))
    def extractName(elem: Element) = elem.attr("href").replace(".html", "").split('$').last.split("/").last
    elems.foldLeft("".asRight[Error]) { (acc, elem) =>
      elem match {
        case TextNode(s) => acc.map(_ + s)
        case ElementNode(e) if isLink(e) => acc.flatMap(a => toMarkdownLink(e).map(a + _))
        case ElementNode(e) => acc.flatMap(a => toMarkdown(e.childNodes, allEntities, linkSolver).map(a + _))
      }
    }
  }
}
