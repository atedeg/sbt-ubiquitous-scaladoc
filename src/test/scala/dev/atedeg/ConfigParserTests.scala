package dev.atedeg

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.syntax.all._

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements", "org.wartremover.warts.Any"))
class ConfigParserTests extends AnyFlatSpec with Matchers {

  "parse" should "fail with an empty string" in {
    ConfigurationParsing.parse("") should matchPattern { case Left(_) => }
  }

  it should "fail with a malformed string" in {
    val malformedString =
      """
        |ignored: []
        |tables:
        | - name: "table1"
        |      """.stripMargin
    ConfigurationParsing.parse(malformedString) should matchPattern { case Left(_) => }
  }

  it should "parse a correct string" in {
    val correctString =
      """
        |ignored:
        | - class: "class"
        | - type: "type"
        | - case: "case"
        | - enum: "enum"
        | - trait: "trait"
        | - def: "def"
        |
        |tables:
        | - name: "table1"
        |   termName: "term"
        |   definitionName: "definition"
        |   rows:
        |     - class: "class"
        |     - trait: "trait"
        |     - enum: "enum"
        |     - type: "type"
        |     - case: "case"
        |     - def: "def"
        | - name: "table2"
        |   rows:
        |     - class: "class"
        |     - trait: "trait"
        |     - enum: "enum"
        |     - type: "type"
        |     - case: "case"
        |     - def: "def"
        |      """.stripMargin

    val rows = List[BaseEntity](
      BaseEntity(Class, "class"),
      BaseEntity(Trait, "trait"),
      BaseEntity(Enum, "enum"),
      BaseEntity(Type, "type"),
      BaseEntity(Case, "case"),
      BaseEntity(Def, "def"),
    )
    val ignored = rows.toSet

    val expected = Configuration(
      ignored,
      List(
        TableConfig("table1", Some("term"), Some("definition"), rows),
        TableConfig("table2", None, None, rows),
      ),
    )
    ConfigurationParsing.parse(correctString) shouldBe expected.asRight[Error]
  }
}
