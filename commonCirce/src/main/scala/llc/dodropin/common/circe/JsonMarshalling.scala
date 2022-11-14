package llc.dodropin.common.circe

import io.circe._
import io.circe.parser._
import io.circe.syntax._

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypeRange, HttpEntity}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}

import scala.concurrent.Future

case class JsonMarshallingImplicits[A]() {
  private val jsonContentType = `application/json`
  private val jsonContentTypes: List[ContentTypeRange] = List(jsonContentType)

  // TODO
  // would like this, but
  // 'could not find lazy implicit value of type'
  // import io.circe.generic.auto._
  // import io.circe.generic.JsonCodec
  // import io.circe.generic.semiauto._
  // import io.circe.syntax._
  // implicit val jsonDecoder: Decoder[A] = deriveDecoder
  // implicit val jsonDecoder: Decoder[A] = deriveDecoder[A.type]
  // implicit val jsonDecoder: Decoder[A] = deriveFor[A]
  // implicit val jsonEncoder: Encoder[A] = derivedEncoder
  // implicit val jsonEncoder: Encoder[A] = deriveEncoder[A.type]

  implicit final def unmarshaller(implicit jsonDecoder: Decoder[A]): FromEntityUnmarshaller[A] =
    Unmarshaller.stringUnmarshaller
      .forContentTypes(jsonContentTypes: _*)
      .flatMap { ctx => mat => json =>
        val _ = (ctx, mat)
        decode(json).fold(Future.failed(_), Future.successful(_))
      }

  implicit final def unmarshallerOpt(implicit jsonDecoder: Decoder[Option[A]]): FromEntityUnmarshaller[Option[A]] =
    Unmarshaller.stringUnmarshaller
      .forContentTypes(jsonContentTypes: _*)
      .flatMap { ctx => mat => json =>
        val _ = (ctx, mat)
        decode(json).fold(Future.failed(_), Future.successful(_))
      }

  implicit final def unmarshallerSeq(implicit jsonDecoder: Decoder[Seq[A]]): FromEntityUnmarshaller[Seq[A]] =
    Unmarshaller.stringUnmarshaller
      .forContentTypes(jsonContentTypes: _*)
      .flatMap { ctx => mat => json =>
        val _ = (ctx, mat)
        decode(json).fold(Future.failed(_), Future.successful(_))
      }

  implicit final def marshaller(implicit jsonEncoder: Encoder[A]): ToEntityMarshaller[A] =
    Marshaller.withFixedContentType(`application/json`) { (a: A) =>
      HttpEntity(jsonContentType, a.asJson.noSpaces)
    }

  implicit final def marshallerOpt(implicit jsonEncoder: Encoder[Option[A]]): ToEntityMarshaller[Option[A]] =
    Marshaller.withFixedContentType(`application/json`) { (a: Option[A]) =>
      HttpEntity(jsonContentType, a.asJson.noSpaces)
    }

  implicit final def marshallerSeq(implicit jsonEncoder: Encoder[Seq[A]]): ToEntityMarshaller[Seq[A]] =
    Marshaller.withFixedContentType(`application/json`) { (a: Seq[A]) =>
      HttpEntity(jsonContentType, a.asJson.noSpaces)
    }

}
