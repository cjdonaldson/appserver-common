package llc.dodropin.common.akka.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PingRoutesControllerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val routes = new PingRoutesController().routes

  "The service" should {

    "leave GET requests to the root path unhandled" in {
      Get() ~> routes ~> check {
        handled shouldBe false
      }
    }

    "return a 'pinged' response for GET requests to /ping" in {
      Get("/ping") ~> routes ~> check {
        responseAs[String] shouldEqual "pinged"
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> routes ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put("/ping") ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}
