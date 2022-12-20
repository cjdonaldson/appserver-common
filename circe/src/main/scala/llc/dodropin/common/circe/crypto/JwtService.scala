// reference: https://www.rfc-editor.org/rfc/rfc7519.html
// header: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9 => {"typ":"JWT","alg":"HS512"}
package llc.dodropin.common.circe.crypto

import llc.dodropin.common.Guid

import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.semiauto._
import io.circe.parser.{decode => circeDecode}
import io.circe.parser

import pdi.jwt.{JwtCirce, JwtAlgorithm}
import pdi.jwt.JwtClaim

import java.time.Clock
import javax.inject.{Inject, Singleton}

case class JwtError(msg: String, e: Option[Throwable] = None) extends Exception(msg, e.orNull)

object JwtService {
  implicit val encoder: Encoder[JwtClaim] = deriveEncoder[JwtClaim]

  val ThirtyMinutes: Long = 30 * 60
}

@Singleton
class JwtService @Inject() (implicit clock: Clock = Clock.systemUTC) extends JwtServiceWithExpiryOffset()(clock, JwtService.ThirtyMinutes)

@Singleton
class JwtServiceWithExpiryOffset @Inject() (implicit clock: Clock = Clock.systemUTC, expireSecondsOffset: Long) {

  private val algorithm = JwtAlgorithm.HS512
  private implicit val jwtDecode: Decoder[JwtClaim] = deriveDecoder[JwtClaim]

  def generate(jwtClaims: Json, secret: String): Either[JwtError, String] =
    circeDecode[JwtClaim](jwtClaims.toString)
      .map(generate(_, secret))
      .left
      .map(toJwtError)

  def generate(jwtClaim: JwtClaim, secret: String): String = {
    val newClaim = jwtClaim
      .merge(sampleJwtClaim)
      .toOption
      .get // fix this
      .expiresIn(expireSecondsOffset) // expiration
      .startsNow // notBefore
      .issuedNow // issuedAt
      .withId(jwtClaim.jwtId.getOrElse(Guid.create.value)) // a unique identification across providers

    JwtCirce.encode(newClaim.toJson, secret, algorithm)
  }

  def sampleJwtClaim = JwtClaim(
    content = "{}",
    issuer = Some("some-service.dodropin.llc"),

    // the entity for which these claims apply; ie: user-id
    subject = Some("noone@dodropin.llc"),
    // resource servers that can be accessed - a possible form of RBAC; admin, agent, dev, devops, ...
    audience = Some(Set("useless.noaccess.core.doropin.llc")),

    // expiration = claims.expiration.orElse(Some(1)),
    // a NumericDate Long seconds or dotted longs days.seconds
    expiration = None,
    notBefore = None,
    issuedAt = None,

    // a unique identification across providers
    jwtId = Some(Guid.create.value)
  )

  def validate(jwt: String, secret: String): Boolean = decode(jwt, secret).map(_.isValid).contains(true)

  def decode(jwt: String, secret: String): Either[JwtError, JwtClaim] = {
    JwtCirce
      .decode(jwt, secret, Seq(algorithm))
      .toEither
      .left
      .map(toJwtError)
  }

  private def toJwtError(e: Throwable) = JwtError(e.getMessage, Some(e))

  implicit class JwtClaimSytax(claim: JwtClaim) {
    def merge(other: JwtClaim): Either[JwtError, JwtClaim] = {
      def parseContent(content: String) = parser.parse(if (claim.content.nonEmpty) claim.content else "{}")

      val n = for {
        cc <- parseContent(claim.content)
        oc <- parseContent(other.content)
      } yield {
        claim
          .by(other.issuer.orElse(claim.issuer).getOrElse("unknown.issuer.dodropin.llc"))
          // the entity for which these claims apply; ie: user-id
          .about(other.subject.orElse(claim.subject).getOrElse("noone@dodropin.llc"))
          // resource servers that can be accessed - a possible form of RBAC; admin, agent, dev, devops, ...
          .to(List(other.audience, claim.audience).flatten.fold(Set.empty)(_ ++ _))
          // json payload extra claims
          .withContent(oc.deepMerge(cc).toString)
          // a unique identification across providers
          .withId(Guid.create.value)
      }
      n.left.map(toJwtError)
    }
  }
}
