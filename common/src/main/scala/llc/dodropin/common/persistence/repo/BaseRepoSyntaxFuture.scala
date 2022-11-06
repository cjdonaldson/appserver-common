package llc.dodropin.common.persistence.repo

import scala.concurrent.Future

trait BaseRepoSyntaxFuture[T] extends BaseRepoSyntax[T, Future] {

  def effectCollectionSuccess(t: Iterable[T]): Future[Iterable[T]] = Future.successful(t)

  def effectSuccess(t: T): Future[T] = Future.successful(t)

  def effectFailed(exception: Exception): Future[T] = Future.failed(exception)

}
