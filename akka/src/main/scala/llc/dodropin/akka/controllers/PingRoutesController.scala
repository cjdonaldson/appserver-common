package llc.dodropin.common.akka.controllers

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

import javax.inject.{Inject, Singleton}
import akka.http.scaladsl.model.StatusCodes

@Singleton
class PingRoutesController @Inject() () {

  val routes: Route = {
    concat(
      path("ping") {
        get {
          complete(StatusCodes.OK, "pinged")
        }
      }
    )
  }
}
