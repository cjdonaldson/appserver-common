package llc.dodropin.common

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.util.ByteString

import org.slf4j.Logger
import org.slf4j.helpers.NOPLogger
import scala.concurrent.Future
import scala.util.{Failure, Success}

object HealthCheckTest extends App {
  implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext = system.executionContext

  val StatusOnlyArg = "--statusOnly"

  var log: Logger = system.log

  val argsList = args.toList
    .flatMap {
      case StatusOnlyArg =>
        log = NOPLogger.NOP_LOGGER
        None

      case s => 
        Some(s)
    }

  log.info("HealthCheck start")
  argsList.foreach(a => log.info(s"arg is: $a"))

  trait HealthStatus {
    val value: Int

    override def toString: String = this.getClass.getSimpleName.takeWhile(_ != '$')
  }
  object Passed extends HealthStatus { val value = 0 }
  object Failed extends HealthStatus { val value = 1 }

  def healthTest(uri: String): Future[HealthStatus] =
    for {
      resp <- Http().singleRequest(HttpRequest(uri = uri))
      _ = log.info(resp.status.toString)
      body <- resp.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
      _ = log.info(body.utf8String)
    } yield if (resp.status.isSuccess) Passed else Failed

  argsList
    .headOption
    .map(healthTest)
    .getOrElse(Future.successful(Failed))
    .onComplete{
      case Success(value) => 
        log.info(s"HealthCheck done: $value")
        sys.exit(value.value)
      
      case Failure(_) => 
        sys.exit(Failed.value)
    }
}
