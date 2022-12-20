package llc.dodropin.common.http4s.controllers

import cats.data.Kleisli
import cats.effect.IO
import org.http4s.{HttpRoutes, Request, Response}
import org.http4s.server.Router

import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.Method.GET

import llc.dodropin.common.Logging

import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

object HealthCheckTimeoutException extends Exception("Health check failed: Timed out")
object HealthCheckFailedException extends Exception("Health check completed: unhealthy")

class HealthCheckRoutesController(healthFutFn: => Future[Boolean], timeout: FiniteDuration)(implicit
    val ec: ExecutionContext,
    val system: ActorSystem
) extends Logging {

  val attempt = {
    val delayedFuture =
      akka.pattern.after(timeout, using = system.scheduler)(Future.failed(HealthCheckTimeoutException))
    val timeoutFuture = Future firstCompletedOf Seq(healthFutFn, delayedFuture)
    timeoutFuture
      .flatMap { b =>
        if (b) Future.successful("Health check ok")
        else Future.failed(HealthCheckFailedException)
      }
      .recoverWith {
        case exception @ HealthCheckFailedException => Future.failed(exception)
        case exception =>
          log.warn("Health check failed: {}", exception.getMessage)
          Future.failed(exception)
      }
  }

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] { case GET -> Root / "health-check" =>
    IO
      .fromFuture(IO(attempt))
      .flatMap(Ok(_))
      .handleErrorWith(e => InternalServerError(e.getMessage))
  }

  val rootRoutes: Kleisli[IO, Request[IO], Response[IO]] = Router("/" -> routes).orNotFound

}

object HealthCheckOptions {
  val defaultTimeout: FiniteDuration = 1.second
  val alwaysHealthy: Future[Boolean] = Future.successful(true)
}
