package llc.dodropin.common.persistence.repo

import llc.dodropin.common.Logging

trait BaseRepoMapperMemImpl[ID, T, E[_]] extends BaseRepoMapper[ID, T, E] with Logging {

  private val repo = scala.collection.mutable.HashMap[ID, List[T]]()

  def getAll: E[Iterable[T]] = effectCollectionSuccess(repo.values.flatten)

  def get(id: ID): E[Iterable[T]] =
    effectCollectionSuccess(repo.get(id).getOrElse(Nil))

  def add(id: ID, t: T): E[Iterable[T]] =
    effectCollectionSuccess {
      log.debug(s"mapper adding $id -> $t")
      repo(id) = t :: repo.get(id).getOrElse(Nil)
      val it = repo(id)
      log.debug(s"mapper added ${!it.isEmpty} $id -> ${it}")
      it
    }

  def remove(id: ID, t: T): E[Iterable[T]] = {
    effectCollectionSuccess(
      repo.get(id) match {
        case None => Nil
        case Some(ts) =>
          repo(id) = ts.filterNot(_ == t)
          repo(id)
      }
    )
  }

}
