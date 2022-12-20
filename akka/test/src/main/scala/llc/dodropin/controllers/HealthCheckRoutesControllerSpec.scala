package llc.dodropin.common.akka.controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.server._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._
import scala.concurrent.Future

class HealthCheckRoutesControllerSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {

  val healthCheckTimeout = 200.millis
  val routes = new HealthCheckRoutesController(HealthCheckOptions.alwaysHealthy, healthCheckTimeout).routes

  val healthCheckUrl = "/health-check"

  "The service" should {

    "leave GET requests to the root path unhandled" in {
      Get() ~> routes ~> check {
        handled shouldBe false
      }
    }

    s"return a ok response for GET requests to $healthCheckUrl" in {
      Get(healthCheckUrl) ~> routes ~> check {
        responseAs[String] shouldEqual "Health check ok"
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> routes ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put(healthCheckUrl) ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET"
      }
    }

    "return a InternalServerError error for a failed health check" in {
      val routes = new HealthCheckRoutesController(Future.successful(false), healthCheckTimeout).routes
      Get(healthCheckUrl) ~> Route.seal(routes) ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual "Health check failed"
      }
    }

    "return a InternalServerError error for a timed out health check" in {
      def lengthyHealthCheck = Future.apply {
        Thread.sleep(5000)
        true
      }
      val routes = new HealthCheckRoutesController(lengthyHealthCheck, healthCheckTimeout).routes
      Get(healthCheckUrl) ~> (routes) ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual "Health check failed: timed out"
      }
    }
  }
}
