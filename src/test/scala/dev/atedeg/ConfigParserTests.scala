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
        |     - name: "col1"
        |       selector: "p"
        |      """.stripMargin
    Configuration.parse(malformedString) should matchPattern { case Left(_) => }
  }
  it should " parse a correct string" in {
    val correctString =
      """
        |ignored:
        | - file: "ignored1"
        | - dir: "ignored2"
        | - glob: "ignored3"
        |
        |tables:
        | - name: "table1"
        |   columns:
        |     - name: "col1"
        |       htmlTag: "sel1"
        |   rows:
        |     - file: "row1"
        |     - dir: "row2"
        |     - glob: "row3"
        | - name: "table2"
        |   columns:
        |     - name: "col2"
        |       htmlTag: "sel2"
        |     - name: "col3"
        |       htmlTag: "sel3"
        |   rows:
        |     - file: "row1"
        |     - dir: "row2"
        |     - glob: "row3"
        |      """.stripMargin

    val ignored = List[Selector](FileSelector("ignored1"), DirSelector("ignored2"), GlobSelector("ignored3"))
    val rows = List[Selector](FileSelector("row1"), DirSelector("row2"), GlobSelector("row3"))
    val expected = Configuration(
      ignored,
      List(
        TableConfig("table1", List(ColumnConfig("col1", "sel1")), rows),
        TableConfig("table2", List(ColumnConfig("col2", "sel2"), ColumnConfig("col3", "sel3")), rows),
      ),
    )
    Configuration.parse(correctString) shouldBe Right(expected)
  }
}
