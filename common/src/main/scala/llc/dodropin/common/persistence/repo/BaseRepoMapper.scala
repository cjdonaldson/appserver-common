package llc.dodropin.common.persistence.repo

import scala.concurrent.Future

trait BaseRepoMapper[ID, T] {

  def getAll: Future[List[T]]

  def get(id: ID): Future[List[T]]

  def add(id: ID, t: T): Future[List[T]]

  def remove(id: ID, t: T): Future[List[T]]
}
