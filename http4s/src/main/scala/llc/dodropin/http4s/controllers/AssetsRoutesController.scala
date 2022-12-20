package llc.dodropin.common.http4s.controllers

import cats.data.Kleisli
import cats.effect.IO
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.Method.GET
import org.http4s.server.Router

class AssetsRoutesController(resourceRoot: String) {
  val resources = AssetResources(resourceRoot)

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case request @ GET -> Root / "favicon.ico"   => resources.getResourceFileImage("favicon.png")(request)
    case request @ GET -> Root / "html" / file   => resources.getResourceFileHtml(file)(request)
    case request @ GET -> Root / "images" / file => resources.getResourceFileImage(file)(request)
    case request @ GET -> Root / "js" / file     => resources.getResourceFileJavascript(file)(request)
  }

  val rootRoutes: Kleisli[IO, Request[IO], Response[IO]] = Router("/" -> routes).orNotFound

}
