package llc.dodropin.common

/*
  Unique hashs that ar easy on humans and database indexing
  UUID is 128 bits represented as 36 chars in the for of 8-4-4-4-12 hex digits
  MD5
  SHA1
  SHA256

  remove `-` from the UUID
  take fragments of the shas

  encode the uuid 128 bits as base 64 -> 24 char, strip `=` -> 22 char
    - 14 fewer chars is more human manageable

  https://preshing.com/20110504/hash-collision-probabilities/

  N = number of bits
  k = number of values (sample size)
  p = 1−e^(−k(k-1)÷(2×2^N))
  50% chance or 1 in 2 chances
  0.5 = 1−e^(−k(k-1)÷(2×2^N))
  1 - 0.5  = e^(−k(k-1)÷(2×2^N))
  ln(0.5)=(−k(k-1)÷(2×2^N))
  (2×2^N)ln(1 + 0.5)=−k(k-1)
  -2×2^N*ln(0.5)]~=k^2
  sqrt[-2×2^N*ln(0.5)]~=k
 */
import java.util.UUID
import java.nio.ByteBuffer
import java.util.Base64
import java.security.SecureRandom

import io.circe.{Decoder, Encoder}

import scala.util.Try

trait GuidMethod
object GuidMethod {
  object UUID extends GuidMethod
  object SecureRandom extends GuidMethod
}

case class Guid private (val value: String)
{
  override def toString = value
}

object Guid {
  def useMethod: GuidMethod = GuidMethod.UUID

  private def guid: Guid =
    useMethod match {
      case GuidMethod.UUID =>
        // uuid is 128 bits (16 bytes)
        // string representation is 36 char; 32 char + 4 `-`
        from(UUID.randomUUID)

      case GuidMethod.SecureRandom =>
        val a = ByteBuffer.allocate(16)
        val rnd = new SecureRandom()
        1.to(2)
          .map(_ => rnd.nextLong())
          .foreach(a.putLong)
        guidFrom(a)
    }

  private def guidFrom(a: ByteBuffer): Guid = {
    assert(a.capacity() >= 16, "byte buffer is insufficient to produce")

    new Guid(
      // base64 encoding will be 22 - 24 char if trail `=` are stripped
      Base64.getUrlEncoder
        .encode(a.array)
        .map(_.toChar)
        .takeWhile(_ != '=')
        .mkString
    )
  }

  def apply(s: String) = new Guid(s)

  def create: Guid = guid

  /** @deprecated
    *   This library uses a random process that encodes a 128 bit PRNG into a human readable value via url/file safe base64 string thus the
    *   minimum safe string is 22 characters.
    *
    * @param length
    *   number of chars to take from a 22 char uid
    * @return
    */
  @Deprecated
  @deprecated("Use with care. Likelihood of collision increases with each char reduction from 22.", "0.0.1")
  def createOfLength(length: Int): Guid = {
    assert(length > 0 && length < 23, "length must be between 1 and 22 inclusive")
    Guid(guid.value.take(length))
  }

  def from(id: String): Guid = new Guid(id)

  def toUuid(guid: Guid): UUID = {
    assert(guid.value.length >= 22, "Guid is not long enough to contain UUID")

    val bytes = Base64.getUrlDecoder().decode(guid.value)

    val bb = ByteBuffer.wrap(bytes)
    val low = bb.getLong();
    val high = bb.getLong();

    new UUID(high, low)
  }

  def from(uuid: UUID): Guid = {
    val bb = ByteBuffer.allocate(16)
    bb.putLong(uuid.getLeastSignificantBits)
    bb.putLong(uuid.getMostSignificantBits)

    guidFrom(bb)
  }

  implicit val guidDecoder =
    Decoder.decodeString.emapTry(str => Try(new Guid(str)))
  implicit val guidEncode = Encoder.encodeString.contramap[Guid](_.value)
}
