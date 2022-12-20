package llc.dodropin.common.http4s.controllers

import cats.effect.unsafe.IORuntime
import org.http4s._
import org.http4s.implicits._

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.actor.ActorSystem

class HealthCheckRoutesControllerSpec extends AnyWordSpec with Matchers {
  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  implicit val system = ActorSystem("test-system")
  import scala.concurrent.ExecutionContext.Implicits.global

  val healthCheckTimeout = 200.millis
  val routes = new HealthCheckRoutesController(HealthCheckOptions.alwaysHealthy, healthCheckTimeout).rootRoutes

  val healthCheckUrl = uri"/health-check"

  "The service" should {

    "leave GET requests to the root path unhandled" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    s"return a ok response for GET requests to $healthCheckUrl" in {
      val result = routes
        .run(Request(method = Method.GET, uri = healthCheckUrl))
        .unsafeRunSync()

      result.status shouldBe Status.Ok
      result.as[String].unsafeRunSync() shouldEqual "Health check ok"
    }

    "leave GET requests to other paths unhandled" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/kermit"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return not found for PUT requests to the root path" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/health-check-no-route"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return a InternalServerError error for a failed health check" in {
      val result = new HealthCheckRoutesController(Future.successful(false), healthCheckTimeout).rootRoutes
        .run(Request(method = Method.GET, uri = healthCheckUrl))
        .unsafeRunSync()

      result.status shouldBe Status.InternalServerError
      result.as[String].unsafeRunSync() shouldEqual "Health check completed: unhealthy"
    }

    "return a InternalServerError error for a timed out health check" in {
      def lengthyHealthCheck = Future.apply {
        Thread.sleep(5000)
        true
      }
      val result = new HealthCheckRoutesController(lengthyHealthCheck, healthCheckTimeout).rootRoutes
        .run(Request(method = Method.GET, uri = healthCheckUrl))
        .unsafeRunSync()

      result.status shouldBe Status.InternalServerError
      result.as[String].unsafeRunSync() shouldEqual "Health check failed: Timed out"
    }
  }
}
