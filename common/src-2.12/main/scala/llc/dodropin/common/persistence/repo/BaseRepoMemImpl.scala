package llc.dodropin.common.persistence.repo

import llc.dodropin.common.Logging
import scala.language.higherKinds

abstract case class BaseRepoMemImpl[ID, T <: RepoId[ID], E[_]](val repoName: String)
    extends BaseRepo[ID, T, E]
    with BaseRepoSyntax[T, E]
    with Logging {

  private val repo = scala.collection.mutable.HashMap[ID, T]()

  def getAll: E[Iterable[T]] = effectCollectionSuccess(repo.values)

  def get(id: ID): E[T] =
    toEffect(repo.get(id))("Nothing found")

  def add(t: T): E[T] = {
    log.debug(s"Adding ${t}")
    repo.get(t.id) match {
      case Some(_) =>
        val exception = RepositoryException.idExists(repoName, t.id)
        log.warn(exception.getMessage)
        effectFailed(exception)

      case None =>
        repo(t.id) = t
        effectSuccess(t)
    }
  }

  def update(t: T): E[T] =
    toEffect(
      repo.get(t.id).map { _ =>
        repo(t.id) = t
        t
      }
    )(s"Unable to update: ${t.id}")

  def remove(id: ID): E[T] =
    toEffect(repo.remove(id))("No result")

}
