package llc.dodropin.common.akka.controllers

import llc.dodropin.common.Logging

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.util.{Success, Failure}

class HealthCheckRoutesController(healthFutFn: => Future[Boolean], timeout: FiniteDuration)(implicit
    val ec: ExecutionContext,
    val system: ActorSystem
) extends Logging {

  val routes: Route = {
    concat(
      path("health-check") {
        get {
          val delayedFuture =
            akka.pattern.after(timeout, using = system.scheduler)(Future.failed(new Exception("timed out")))
          val timeoutFuture = Future firstCompletedOf Seq(healthFutFn, delayedFuture)

          onComplete(timeoutFuture) {
            case Success(status) if status => complete(StatusCodes.OK, "Health check ok")

            case Success(_) =>
              val message = "Health check failed"
              log.warn(message)
              complete(StatusCodes.InternalServerError, message)

            case Failure(exception) =>
              val message = s"Health check failed: ${exception.getMessage}"
              log.warn(message)
              complete(StatusCodes.InternalServerError, message)
          }
        }
      }
    )
  }
}

object HealthCheckOptions {
  val defaultTimeout: FiniteDuration = 1.second
  val alwaysHealthy: Future[Boolean] = Future.successful(true)
}
