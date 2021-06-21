package llc.dodropin.common.persistence.repo

import scala.concurrent.Future
import org.slf4j.Logger

class RepositoryException[ID](repoName: String, id: Option[ID], message: String) extends Exception(message)

object RepositoryException {
  def apply(repoName: String, message: String): RepositoryException[Any] = new RepositoryException(repoName, None, message)

  def apply[ID](repoName: String, id: ID, message: String): RepositoryException[ID] = new RepositoryException(repoName, Some(id), message)

  def idExists[ID](repoName: String, id: ID): RepositoryException[ID] = new RepositoryException(repoName, Some(id), s"$id exists")
}

trait BaseRepo[ID, T] {
  type TwithGetId = T { def id: ID }

  val log: Logger

  def repoName: String

  def getAll: Future[Seq[T]]

  def get(id: ID): Future[T]

  def add(t: TwithGetId): Future[T]

  def update(t: TwithGetId): Future[T]

  def remove(id: ID): Future[T]

  def toFuture(t: Option[T])(failMessage: String): Future[T] =
    t
      .map(Future.successful)
      .getOrElse {
        val exception =
          t
            .fold(RepositoryException(repoName, failMessage))(RepositoryException(repoName, _, failMessage))
        log.warn(exception.getMessage)
        Future.failed(exception)
      }

}