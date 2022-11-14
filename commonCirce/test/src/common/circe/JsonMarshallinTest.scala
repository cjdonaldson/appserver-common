package llc.dodropin.common

import org.scalatest.funspec.AnyFunSpec

import llc.dodropin.common.circe.JsonMarshallingImplicits

// how to move these into JsonMarshallingImplicits?
import io.circe.generic.auto._
import io.circe.syntax._

class JsonMarshallingTest extends AnyFunSpec {
  case class TestClass(s: String, i: Int)

  val testClass = TestClass("as string", 200)

  val json =
    """{
      |  "s" : "as string",
      |  "i" : 200
      |}""".stripMargin

  val jsonOpt = "{}"

  val jsonArray =
    """[
      |  {
      |    "s" : "as string",
      |    "i" : 200
      |  },
      |  {
      |    "s" : "as string",
      |    "i" : 200
      |  }
      |]
      |""".stripMargin

  val impl = JsonMarshallingImplicits[TestClass]()

  describe("JsonMarshalling") {
    it("should create json from instance") {
      assert(testClass.asJson.toString == json)
    }

    it("should parse json to instance") {
      assert(
        io.circe.parser
          .parse(json)
          .flatMap(_.as[TestClass])
          .contains(testClass)
      )
    }

    it("should parse json to option instance") {
      assert(
        io.circe.parser
          .parse(json)
          .flatMap(_.as[Option[TestClass]])
          .contains(Some(testClass))
      )
    }

    it("should parse json array to instances") {
      assert(
        io.circe.parser
          .parse(jsonArray)
          .flatMap(_.as[List[TestClass]])
          .contains(List(testClass, testClass))
      )
    }

  }

}
