package llc.dodropin.common.circe

import io.circe._
import io.circe.parser._
import io.circe.syntax._

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypeRange, HttpEntity}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}

import scala.concurrent.Future

case class JsonMarshalling[A]() {
  // would like this, but
  // 'could not find lazy implicit value of type'
  // import io.circe.generic.semiauto._
  // implicit val jsonDecoder = deriveDecoder[A.type]
  // implicit val jsonEncoder = deriveEncoder[A.type]

  private def jsonContentTypes: List[ContentTypeRange] =
    List(`application/json`)

  implicit final def unmarshaller(implicit jsonDecoder: Decoder[A]): FromEntityUnmarshaller[A] =
    Unmarshaller.stringUnmarshaller
      .forContentTypes(jsonContentTypes: _*)
      .flatMap { ctx => mat => json =>
        val _ = (ctx, mat)
        decode(json).fold(Future.failed(_), Future.successful(_))
      }

  implicit final def marshaller(implicit jsonEncoder: Encoder[A]): ToEntityMarshaller[A] =
    Marshaller.withFixedContentType(`application/json`) { (a: A) =>
      HttpEntity(`application/json`, a.asJson.noSpaces)
    }
}
