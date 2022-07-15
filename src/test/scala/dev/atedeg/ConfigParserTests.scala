package dev.atedeg

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class ConfigParserTests extends AnyFlatSpec with Matchers {

  "parse" should " fail with an empty string" in {
    Configuration.parse("") should matchPattern { case Left(_) => }
  }

  it should " fail with a malformed string" in {
    val malformedString =
      """
        |ignored: []
        |tables:
        | - name: "table1"
        |   columns:
        |      """.stripMargin
    Configuration.parse(malformedString) should matchPattern { case Left(_) => }
  }

  it should " parse a correct string" in {
    val correctString =
      """
        |ignored:
        | - className: "class"
        | - typeName: "type"
        | - caseName: "case"
        | - enumName: "enum"
        | - traitName: "trait"
        |
        |tables:
        | - name: "table1"
        |   termName: "term"
        |   definitionName: "definition"
        |   rows:
        |     - className: "class"
        |     - typeName: "type"
        |       lookupFile: "file"
        |     - caseName: "case"
        |       lookupFile: "file"
        |     - enumName: "enum"
        |     - traitName: "trait"
        | - name: "table2"
        |   rows:
        |     - className: "class"
        |     - typeName: "type"
        |       lookupFile: "file"
        |     - caseName: "case"
        |       lookupFile: "file"
        |     - enumName: "enum"
        |     - traitName: "trait"
        |      """.stripMargin

    val ignored = Set[IgnoredSelector](
      IgnoredClass("class"),
      IgnoredType("type"),
      IgnoredEnumCase("case"),
      IgnoredEnum("enum"),
      IgnoredTrait("trait"),
    )
    val rows = List[Selector](
      Class("class"),
      Type("type", "file"),
      EnumCase("case", "file"),
      Enum("enum"),
      Trait("trait"),
    )

    val expected = Configuration(
      ignored,
      List(
        TableConfig("table1", Some("term"), Some("definition"), rows),
        TableConfig("table2", None, None, rows),
      ),
    )
    Configuration.parse(correctString) shouldBe Right(expected)
  }
}
