package llc.dodropin.common.persistence.repo

import llc.dodropin.common.Logging

import scala.concurrent.Future

abstract case class BaseRepoMemImpl[ID, T <: RepoId[ID]](val repoName: String) extends BaseRepo[ID, T] with Logging {
  log.info(s"repo init for $repoName")

  private val repo = scala.collection.mutable.Map[ID, T]()

  def getAll: Future[Seq[T]] = Future.successful(repo.values.toSeq)

  def get(id: ID): Future[T] =
    toFuture(repo.get(id))("Nothing found")

  def add(t: T): Future[T] = {
    log.debug(s"Adding ${t}")
    repo.get(t.id) match {
      case Some(_) =>
        val exception = RepositoryException.idExists(repoName, t.id)
        log.warn(exception.getMessage)
        Future.failed(exception)

      case None =>
        repo(t.id) = t
        Future.successful(t)
    }
  }

  def update(t: T): Future[T] =
    toFuture(
      repo.get(t.id).map { _ =>
        repo(t.id) = t
        t
      }
    )(s"Unable to update: ${t.id}")

  def remove(id: ID): Future[T] =
    toFuture(repo.remove(id))("No result")

}
