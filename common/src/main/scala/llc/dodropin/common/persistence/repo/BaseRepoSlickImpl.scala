package llc.dodropin.common.persistence.repo

import llc.dodropin.common.Logging

import scala.concurrent.Future
import slick.lifted.TableQuery
import slick.lifted.AbstractTable

case class BaseRepoSlickImpl[ID, T <: RepoId[ID] with AbstractTable[_]](
    val repoName: String,
    repo1: TableQuery[T]
) extends BaseRepo[ID, T]
    with Logging {
  log.info(s"repo init for $repoName")

  private val repo = scala.collection.mutable.Map[ID, T]()

  def getAll: Future[Seq[T]] = Future.successful(repo.values.toSeq)

  def get(id: ID): Future[T] =
    toFuture(repo.get(id))("Nothing found")

  def add(t: T): Future[T] = {
    repo.get(t.id) match {
      case Some(_) =>
        val exception = RepositoryException.idExists(repoName, t.id)
        log.warn(exception.getMessage)
        Future.failed(exception)

      case None =>
        log.info(s"$repoName adding ${t}")
        repo(t.id) = t
        Future.successful(t)
    }
  }

  def update(t: T): Future[T] = toFuture {
    repo.get(t.id).map { _ =>
      repo(t.id) = t
      t
    }
  }(s"Unable to update: ${t.id} ")

  def remove(id: ID): Future[T] =
    toFuture(repo.remove(id))("No result")

}
