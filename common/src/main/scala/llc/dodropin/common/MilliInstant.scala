package llc.dodropin.common

import java.time.Instant
import java.time.temporal.ChronoUnit

case class MilliInstant private (value: Instant) extends AnyVal {
  def plusSeconds(seconds: Long) = copy(value = value.plusSeconds(seconds))
}

object MilliInstant {
  def apply(instant: Instant): MilliInstant = new MilliInstant(truncated(instant))

  def now: MilliInstant = apply(Instant.now)

  private def truncated(instant: Instant) = instant.truncatedTo(ChronoUnit.MILLIS)
}
