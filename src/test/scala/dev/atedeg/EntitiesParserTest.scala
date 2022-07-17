package dev.atedeg

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import cats.syntax.all._

@SuppressWarnings(Array(
  "org.wartremover.warts.NonUnitStatements",
  "org.wartremover.warts.Any"))
class EntitiesParserTest extends AnyFlatSpec with Matchers {

  "parse" should "fail with an empty string" in {
    EntityParsing.parse("") should matchPattern { case Left(_) => }
  }

  it should "fail with a malformed string" in {
    val malformedString = "pages: [error]"
    EntityParsing.parse(malformedString) should matchPattern { case Left(_) => }
  }

  it should "parse a correct string" in {
    val correctString =
      """
        |pages = [{
        |        "l": "link",
        |        "e": false,
        |        "i": "",
        |        "n": "Type",
        |        "t": "",
        |        "d": "package",
        |        "k": "type"
        |    },
        |{
        |        "l": "link",
        |        "e": false,
        |        "i": "",
        |        "n": "Enum",
        |        "t": "",
        |        "d": "package",
        |        "k": "enum"
        |    },
        |    {
        |        "l": "link",
        |        "e": false,
        |        "i": "",
        |        "n": "Case",
        |        "t": "",
        |        "d": "package",
        |        "k": "case"
        |    },
        |    {
        |        "l": "link",
        |        "e": false,
        |        "i": "",
        |        "n": "Trait",
        |        "t": "",
        |        "d": "package",
        |        "k": "trait"
        |    },
        |    {
        |        "l": "link",
        |        "e": false,
        |        "i": "",
        |        "n": "Class",
        |        "t": "",
        |        "d": "package",
        |        "k": "class"
        |    },
        |    {
        |        "l": "link",
        |        "e": false,
        |        "i": "",
        |        "n": "Def",
        |        "t": "",
        |        "d": "package",
        |        "k": "def"
        |    }];""".stripMargin
    val expected: Set[Entity] = Set(
      Entity(Class, "link", "Class", "package"),
      Entity(Enum, "link", "Enum", "package"),
      Entity(Trait, "link", "Trait", "package"),
      Entity(Type, "link", "Type", "package"),
      Entity(Case, "link", "Case", "package"),
      Entity(Def, "link", "Def", "package"),
    )
    EntityParsing.parse(correctString) shouldBe expected.asRight[Error]
  }
}
