package llc.dodropin.common.http4s.controllers

import cats.effect.unsafe.IORuntime

import org.http4s._
import org.http4s.implicits._

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PingRoutesControllerSpec extends AnyWordSpec with Matchers {
  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  val routes = new PingRoutesController().rootRoutes

  "The service" should {

    "not found for GET requests to the root path" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return a 'pinged' response for GET requests to /ping" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/ping"))
        .unsafeRunSync()

      result.status shouldBe Status.Ok
      result.as[String].unsafeRunSync() shouldEqual "pinged"
    }

    "return not found for GET requests to other paths" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/kermit"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return a not found for PUT requests to the root path" in {
      val result = routes
        .run(Request(method = Method.PUT, uri = uri"/ping"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }
  }
}
