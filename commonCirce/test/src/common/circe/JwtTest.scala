package common.circe

import llc.dodropin.common.circe.crypto.JwtService

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import io.circe.parser
import pdi.jwt.JwtClaim
import java.time.{Clock, Instant, ZonedDateTime, ZoneId}
import java.util.Base64

class JwtTest extends AnyFunSpec with Matchers {

  val secret = "secretKey"

  val jwtValid =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJzb21lLXNlcnZpY2UuZG9kcm9waW4ubGxjIiwic3ViIjoibm9vbmVAZG9kcm9waW4ubGxjIiwiYXVkIjpbInVzZWxlc3Mubm9hY2Nlc3MuY29yZS5kb3JvcGluLmxsYyIsImF1ZGllbmNlcyIsInNvbWUtc2VydmljZS5kb2Ryb3Bpbi5sbGMiLCJzb21lLW90aGVyLXNlcnZpY2UuZG9kcm9waW4ubGxjL3VzZXJzIl0sImV4cCI6MTY2OTU0NTAwMCwibmJmIjoxNjY5NTQzMjAwLCJpYXQiOjE2Njk1NDMyMDAsImp0aSI6ImEtdGVzdC1zdGFibGUtaWQifQ.iJPcZZven6fr4frNAQPECICogA7trKgvzMShYaxTaAhLiz8MdCahF2O2dYYtSc6CXMMiEHLgwa75WL70oU42jA"

  val clock: Clock = new Clock {
    override def getZone(): ZoneId = ???

    override def instant(): Instant = ZonedDateTime.parse("2022-11-27T10:00:00.000Z").toInstant

    override def withZone(zone: ZoneId): Clock = ???
  }

  describe("Jwt") {
    it("encode a valid active jwt") {
      val claims = JwtClaim(
        content = "",
        issuer = Some("some-service.dodropin.llc"),
        subject = Some("chaz.d@dodropin.llc"),
        audience = Some(Set("audiences", "some-service.dodropin.llc", "some-other-service.dodropin.llc/users")),
        expiration = None,
        notBefore = None,
        issuedAt = None,
        jwtId = Some("a-test-stable-id")
      )
      val jwt = new JwtService()(clock).generate(claims, secret)
      val encodedClaims = jwt.split("\\.").drop(1).head
      info(s"encoded claim: $encodedClaims")

      val decodeStr = new String(Base64.getDecoder.decode(encodedClaims))
      val json = parser.parse(decodeStr).toOption.get
      info(json.spaces2)

      jwt shouldBe jwtValid
    }

    it("decode a valid active jwt") {
      val valid500years =
        "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJjb3JlLmRvZHJvcGluLmxsYyIsInN1YiI6InN0cmVhbWVyIiwiYXVkIjoidGVzdGluZyBpdCIsImV4cCI6MTc0NDc5OTc2MDAsIm5iZiI6MTY2OTU0MzIwMCwiaWF0IjoxNjY5NTQzMjAwLCJqdGkiOiJhLXRlc3Qtc3RhYmxlLWlkIn0.1i26-BBk3kcdrPV1ct1sf5O3DUoUg0kwctiinqCcS-MKnIK_rojpFPZ09TWVLferHAKeYv0o9ao5rtruQtIe0w"
      new JwtService()(clock).decode(valid500years, secret).toOption.get shouldBe JwtClaim(
        content = "{}",
        issuer = Some("core.dodropin.llc"),
        subject = Some("streamer"),
        audience = Some(Set("testing it")),
        expiration = Some(17447997600L),
        notBefore = Some(1669543200),
        issuedAt = Some(1669543200),
        jwtId = Some("a-test-stable-id")
      )
    }

    it("decode an expired jwt") {
      new JwtService()(clock)
        .decode(jwtValid, secret)
        .swap
        .toOption
        .get
        .getMessage shouldBe ("The token is expired since 2022-11-27T10:30:00Z")
    }
  }

}
