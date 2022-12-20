package llc.dodropin.common.http4s.controllers

import cats.effect.unsafe.IORuntime

import org.http4s._
import org.http4s.implicits._

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class AssetsRoutesControllerSpec extends AnyWordSpec with Matchers {
  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  val routes = new AssetsRoutesController("/common/test/resources/").rootRoutes

  "The service" should {

    "leave GET requests to the root path unhandled" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return a file content response for GET requests to /html/<file.html>" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/html/test.html"))
        .unsafeRunSync()

      result.status shouldBe Status.Ok
      result.as[String].unsafeRunSync() shouldEqual "html test\n"
    }

    "leave GET requests to other paths unhandled" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/kermit"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return a image for GET requests to /images/<file>" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/images/test"))
        .unsafeRunSync()

      result.status shouldBe Status.Ok
      result.as[String].unsafeRunSync() shouldEqual "image test\n"
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      val result = routes
        .run(Request(method = Method.PUT, uri = uri"/image/test"))
        .unsafeRunSync()

      result.status shouldBe Status.NotFound
    }

    "return a js for GET requests to /js/<file>" in {
      val result = routes
        .run(Request(method = Method.GET, uri = uri"/js/javascript.js"))
        .unsafeRunSync()

      result.status shouldBe Status.Ok
      result.as[String].unsafeRunSync() shouldEqual "js test\n"
    }

  }
}
