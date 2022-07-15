package dev.atedeg

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

@SuppressWarnings(Array("org.wartremover.warts.NonUnitStatements"))
class EntitiesParserTest extends AnyFlatSpec with Matchers {

  "parse" should "fail with an empty string" in {
    AllEntities.parse("") should matchPattern { case Left(_) => }
  }

  it should "fail with a malformed string" in {
    val malformedString = "pages: [error]"
    AllEntities.parse(malformedString) should matchPattern { case Left(_) => }
  }

  it should "parse a correct string" in {
    val correctString =
      """
        |pages = [{
        |        "l": "",
        |        "e": false,
        |        "i": "",
        |        "n": "Type",
        |        "t": "",
        |        "d": "",
        |        "k": "type"
        |    },
        |{
        |        "l": "",
        |        "e": false,
        |        "i": "",
        |        "n": "Enum",
        |        "t": "",
        |        "d": "",
        |        "k": "enum"
        |    },
        |    {
        |        "l": "",
        |        "e": false,
        |        "i": "",
        |        "n": "Case",
        |        "t": "",
        |        "d": "",
        |        "k": "case"
        |    },
        |    {
        |        "l": "",
        |        "e": false,
        |        "i": "",
        |        "n": "Trait",
        |        "t": "",
        |        "d": "",
        |        "k": "trait"
        |    },
        |    {
        |        "l": "",
        |        "e": false,
        |        "i": "",
        |        "n": "Class",
        |        "t": "",
        |        "d": "",
        |        "k": "class"
        |    },
        |    {
        |        "n": "ignore me",
        |        "k": "package"
        |    }];""".stripMargin
    val expected: Set[IgnoredSelector] = Set(
      IgnoredClass("Class"),
      IgnoredEnum("Enum"),
      IgnoredTrait("Trait"),
      IgnoredEnumCase("Case"),
      IgnoredType("Type"),
    )
    AllEntities.parse(correctString) shouldBe Right(expected)
  }
}
