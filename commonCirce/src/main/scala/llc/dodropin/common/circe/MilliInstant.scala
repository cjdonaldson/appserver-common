package llc.dodropin.common.circe

import llc.dodropin.common.{MilliInstant => MilliInstantType}

import io.circe.generic.semiauto._

object MilliInstant {
  implicit val jsonMilliInsantDecode = deriveDecoder[MilliInstantType]
  implicit val jsonMilliInsantEncode = deriveEncoder[MilliInstantType]
}
