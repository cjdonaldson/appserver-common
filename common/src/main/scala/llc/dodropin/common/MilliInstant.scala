package llc.dodropin.common

import java.time.Instant
import java.time.temporal.ChronoUnit

final case class MilliInstant() {
    def now = Instant.now.truncatedTo(ChronoUnit.MILLIS)
}
