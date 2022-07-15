package dev.atedeg

import better.files.File
import net.ruippeixotog.scalascraper.browser.{ Browser, JsoupBrowser }
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.*
import net.ruippeixotog.scalascraper.model.*
import net.ruippeixotog.scalascraper.dsl.DSL.*
import cats.syntax.all.*

object HtmlParsing {

  def extractClassLike(file: File): Either[Error, (String, String)] = for {
    document <- JsoupBrowser().parseFile(file.toJava).asRight
    term <- extractTagFromDocument(file, document, "title")
    definition <- extractTagFromDocument(file, document, "div.doc > p")
  } yield (term, definition)

  private def extractTagFromElem(file: File, elem: Element, tag: String): Either[Error, String] =
    elem.tryExtract(element(tag)).map(_.childNodes).map(toMarkdown).toRight(ParseError(file, tag))

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

  private def extractMany(file: File, doc: Browser#DocumentType, tag: String): Either[Error, List[Element]] =
    doc.tryExtract(elementList(tag)).toRight(ParseError(file, tag))

  def extractNonClassLike(file: File, name: String): Either[Error, (String, String)] = for {
    document <- JsoupBrowser().parseFile(file.toJava).asRight
    elements <- extractMany(file, document, "div.documentableElement")
    definition <- elements
      .find(hasName(_, name))
      .toRight(ParseError(file, "a.documentableName"))
      .flatMap(extractTagFromElem(file, _, "div.cover > div.doc"))
  } yield (name, definition)

  private def hasName(elem: Element, name: String): Boolean = elem.tryExtract(text("a.documentableName")).contains(name)
}
