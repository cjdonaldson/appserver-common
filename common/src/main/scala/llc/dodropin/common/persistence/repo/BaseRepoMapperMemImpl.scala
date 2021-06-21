package llc.dodropin.common.persistence.repo

import llc.dodropin.common.Logging

import scala.concurrent.Future

abstract case class BaseRepoMapperMemImpl[ID, T]() extends BaseRepoMapper[ID, T] with Logging {

  private val repo = scala.collection.mutable.Map[ID, List[T]]()

  def getAll: Future[List[T]] = Future.successful(repo.values.toList.flatten)

  def get(id: ID): Future[List[T]] =
    Future.successful(repo.get(id).getOrElse(Nil))

  def add(id: ID, t: T): Future[List[T]] =
    Future.successful {
      log.debug(s"mapper adding $id -> $t")
      repo(id) = t :: repo.get(id).getOrElse(Nil)
      val it = repo(id)
      log.debug(s"mapper added ${!it.isEmpty} $id -> ${it}")
      it
    }

  def remove(id: ID, t: T): Future[List[T]] = {
    Future.successful(
      repo.get(id) match {
        case None => Nil
        case Some(ts) =>
          repo(id) = ts.filterNot(_ == t)
          repo(id)
      }
    )
  }

}
