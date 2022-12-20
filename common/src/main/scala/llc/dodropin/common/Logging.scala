package llc.dodropin.common

import org.slf4j.LoggerFactory
import org.slf4j.Logger

trait Logging {
  val log: Logger = {
    val name = this.getClass.getName.split("\\$").head
    LoggerFactory.getLogger(name);
  }
}
