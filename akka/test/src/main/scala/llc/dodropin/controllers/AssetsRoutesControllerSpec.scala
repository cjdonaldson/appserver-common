package llc.dodropin.common.akka.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AssetsRoutesControllerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val routes = new AssetsRoutesController("/common/test/resources/").routes

  "The service" should {

    "leave GET requests to the root path unhandled" in {
      Get() ~> routes ~> check {
        handled shouldBe false
      }
    }

    "return a file content response for GET requests to /html/<file.html>" in {
      Get("/html/test.html") ~> routes ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[String] shouldEqual "html test\n"
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> routes ~> check {
        handled shouldBe false
      }
    }

    "return a image for GET requests to /images/<file>" in {
      Get("/images/test") ~> Route.seal(routes) ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[String] shouldEqual "image test\n"
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put("/image/test") ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.NotFound
        responseAs[String] shouldEqual "The requested resource could not be found."
      }
    }

    "return a js for GET requests to /js/<file>" in {
      Get("/js/javascript.js") ~> Route.seal(routes) ~> check {
        handled shouldBe true
        status shouldBe StatusCodes.OK
        responseAs[String] shouldEqual "js test\n"
      }
    }
  }
}
