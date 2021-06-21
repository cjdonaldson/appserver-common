package llc.dodropin.util

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn
import scala.util.Try

case class QuickStartServer(host: String, port: Int, routes: Route, actorSystem: ActorSystem) {
  implicit val system: ActorSystem = actorSystem

  // Needed for the Future and its methods flatMap/onComplete in the end
  implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  val serverBindingFuture: Future[ServerBinding] = Http().newServerAt(host, port).bind(routes)

  Try {
    actorSystem.log.info(s"java version: ${System.getProperty("java.runtime.version")}")
    actorSystem.log.info(s"scala version: ${util.Properties.versionString}")
    actorSystem.log.info(s"Server online at http://$host:$port/\nType quit to stop...")

    while (StdIn.readLine().trim != "quit") {}

    serverBindingFuture
      .flatMap(_.unbind())
      .onComplete { done =>
        done.failed.map(ex => actorSystem.log.error(ex, "Failed unbinding"))
        actorSystem.terminate()
      }
  }.recover { case _: NullPointerException => actorSystem.log.info("StdIn is undefined. Running as daemon") }
}
