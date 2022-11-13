package llc.dodropin.common

import scala.concurrent.ExecutionContext
import java.util.concurrent.Executors
import javax.inject.Singleton

@Singleton
final case class BlockingExecutionContext() extends ExecutionContext with Logging {
  val threadPool = Executors.newFixedThreadPool(5)

  def execute(runnable: Runnable) = {
    threadPool.submit(runnable)
    ()
  }

  def reportFailure(t: Throwable) = {
    log.warn("Execution context failure {}", t.getMessage)
    throw t
  }
}
