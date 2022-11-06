package llc.dodropin.common

import org.slf4j.LoggerFactory
import org.slf4j.Logger

trait Logging {
  val log: Logger = LoggerFactory.getLogger(this.getClass.getSimpleName.dropRight(1));
}
