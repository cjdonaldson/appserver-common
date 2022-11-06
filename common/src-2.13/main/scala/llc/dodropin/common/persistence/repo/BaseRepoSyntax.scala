package llc.dodropin.common.persistence.repo

import llc.dodropin.common.Logging

trait BaseRepoSyntax[T, E[_]] extends Logging {

  def repoName: String

  def effectCollectionSuccess(t: Iterable[T]): E[Iterable[T]]

  def effectSuccess(t: T): E[T]

  def effectFailed(exception: Exception): E[T]

  def toEffect(t: Option[T])(failMessage: String): E[T] =
    t
      .map(effectSuccess)
      .getOrElse {
        val exception =
          t.fold(RepositoryException(repoName, failMessage))(RepositoryException(repoName, _, failMessage))
        log.warn(exception.getMessage)
        effectFailed(exception)
      }

}
