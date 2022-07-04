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
        |ignored: ["ignored1", "ignored2"]
        |tables:
        | - name: "table1"
        |   columns:
        |     - name: "col1"
        |       selector: "sel1"
        |   rows:
        |     - "row1"
        |     - "row2"
        | - name: "table2"
        |   columns:
        |     - name: "col2"
        |       selector: "sel2"
        |     - name: "col3"
        |       selector: "sel3"
        |   rows:
        |     - "row3"
        |     - "row4"
        |      """.stripMargin
    val expected = Configuration(
      List("ignored1", "ignored2").map(Selector(_)),
      List(
        TableConfig(
          "table1",
          List(ColumnConfig("col1", "sel1")),
          List("row1", "row2").map(Selector(_)),
        ),
        TableConfig(
          "table2",
          List(ColumnConfig("col2", "sel2"), ColumnConfig("col3", "sel3")),
          List("row3", "row4").map(Selector(_)),
        ),
      ),
    )
    Configuration.parse(correctString) shouldBe Right(expected)
  }
}
