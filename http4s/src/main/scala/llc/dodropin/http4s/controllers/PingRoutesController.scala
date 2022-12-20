package llc.dodropin.common.http4s.controllers

import cats.data.Kleisli
import cats.effect.IO
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.Router

import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.Method.GET
import com.google.inject.{Inject, Singleton}

@Singleton
class PingRoutesController @Inject() () {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "ping" =>
    Ok("pinged")
  }

  val rootRoutes: Kleisli[IO, Request[IO], Response[IO]] = Router("/" -> routes).orNotFound

}
