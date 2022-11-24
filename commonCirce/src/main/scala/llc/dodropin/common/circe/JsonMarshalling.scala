package llc.dodropin.common.circe

import io.circe._
import io.circe.parser._
import io.circe.syntax._

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentTypeRange, HttpEntity}
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.http.scaladsl.unmarshalling.FromResponseUnmarshaller

import scala.concurrent.Future
// import akka.http.scaladsl.model.HttpResponse

object JsonMarshallingImplicits { //[A]()(implicit decoder: Decoder[A], encoder: Encoder[A]) {
// case class JsonMarshallingImplicits[A]()(implicit decoder: Decoder[A], encoder: Encoder[A]) {
  // private final val self = this

  private val jsonContentType = `application/json`
  private val jsonContentTypes: List[ContentTypeRange] = List(jsonContentType)

  // private implicit val x: Decoder[A] = ??? //implicitly[Decoder[A]]

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

  // implicit class DecoderImplicits[A : Decoder](a: A) {
    implicit def unmarshaller[A: Decoder]: FromEntityUnmarshaller[A] =
      Unmarshaller.stringUnmarshaller
        .forContentTypes(jsonContentTypes: _*)
        .flatMap { ctx => mat => json =>
          val _ = (ctx, mat)
          // implicit val xx = implicitly[Decoder[A]]
          decode(json).fold(Future.failed(_), Future.successful(_))
        }

    implicit def unmarshallerOpt[A: Decoder]: FromEntityUnmarshaller[Option[A]] =
      Unmarshaller.stringUnmarshaller
        .forContentTypes(jsonContentTypes: _*)
        .flatMap { ctx => mat => json =>
          val _ = (ctx, mat)
          decode(json).fold(Future.failed(_), a => Future.successful(Some(a)))
        }

    implicit def unmarshallerSeq[A: Decoder]: FromEntityUnmarshaller[Seq[A]] =
      Unmarshaller.stringUnmarshaller
        .forContentTypes(jsonContentTypes: _*)
        .flatMap { ctx => mat => json =>
          val _ = (ctx, mat)
          decode(json).fold(Future.failed(_), a => Future.successful(Seq(a)))
        }
  // }
    implicit def unmarshallerR[A: Decoder]: FromResponseUnmarshaller[A] =
      // (res: HttpResponse) => res.entity
      Unmarshaller.messageUnmarshallerFromEntityUnmarshaller
      // Unmarshaller.stringUnmarshaller
      //   .forContentTypes(jsonContentTypes: _*)
      //   .flatMap { ctx => mat => json =>
      //     val _ = (ctx, mat)
      //     // implicit val xx = implicitly[Decoder[A]]
      //     decode(json).fold(Future.failed(_), Future.successful(_))
      //   }

    implicit def unmarshallerOptR[A: Decoder]: FromResponseUnmarshaller[Option[A]] =
      Unmarshaller.messageUnmarshallerFromEntityUnmarshaller
      // Unmarshaller.stringUnmarshaller
      //   .forContentTypes(jsonContentTypes: _*)
      //   .flatMap { ctx => mat => json =>
      //     val _ = (ctx, mat)
      //     decode(json).fold(Future.failed(_), a => Future.successful(Some(a)))
      //   }

    implicit def unmarshallerSeqR[A: Decoder]: FromResponseUnmarshaller[Seq[A]] =
      Unmarshaller.messageUnmarshallerFromEntityUnmarshaller
      // (response: HttpResponse) => unmarshaller[A].unmarshal(response.entity, ???)
      // Unmarshaller(response.entity).to[Seq[A]]//.FromResponseUnmarshaller
      // .stringUnmarshaller
      // .
        // .forContentTypes(jsonContentTypes: _*)
        // .flatMap { ctx => mat => json =>
          // val _ = (ctx, mat)
          // decode(json).fold(Future.failed(_), a => Future.successful(Seq(a)))
        // }


  // implicit class XxXEncoderImplicits[A : Encoder](a: A) {
    implicit def marshaller[A: Encoder]: ToEntityMarshaller[A] =
      Marshaller.withFixedContentType(`application/json`) { (a) =>
        HttpEntity(jsonContentType, a.asJson.noSpaces)
      }

    implicit def marshallerOpt[A: Encoder]: ToEntityMarshaller[Option[A]] =
      Marshaller.withFixedContentType(`application/json`) { (a: Option[A]) =>
        HttpEntity(jsonContentType, a.asJson.noSpaces)
      }

    implicit def marshallerSeq[A: Encoder]: ToEntityMarshaller[Seq[A]] =
      Marshaller.withFixedContentType(`application/json`) { (a: Seq[A]) =>
        HttpEntity(jsonContentType, a.asJson.noSpaces)
      }
  // }

  // object Implicits {
  //   implicit def marshaller: ToEntityMarshaller[A] = self.marshaller
  //   implicit def marshallerOpt: ToEntityMarshaller[Option[A]] = self.marshallerOpt
  //   implicit def marshallerSeq: ToEntityMarshaller[Seq[A]] = self.marshallerSeq

  //   implicit def unmarshaller: FromEntityUnmarshaller[A] = self.unmarshaller
  //   implicit def unmarshallerOpt: FromEntityUnmarshaller[Option[A]] = self.unmarshallerOpt
  //   implicit def unmarshallerSeq: FromEntityUnmarshaller[Seq[A]] = self.unmarshallerSeq
  // }
}
