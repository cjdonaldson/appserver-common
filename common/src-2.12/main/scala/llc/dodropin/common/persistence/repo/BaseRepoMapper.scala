package llc.dodropin.common.persistence.repo

import scala.language.higherKinds

/** BaseRepoMapper providing CRUD contract in an Effect for an Entity pairing [ID, Iterable[T]]
  *
  * @param ID
  *   the type for the id to use: Guid, Int, String
  * @param T
  *   the type to be presented
  * @param E
  *   the effect system: Future, IO, ...
  */
trait BaseRepoMapper[ID, T, E[_]] extends BaseRepoSyntax[T, E] {

  def getAll: E[Iterable[T]]

  def get(id: ID): E[Iterable[T]]

  def add(id: ID, t: T): E[Iterable[T]]

  def remove(id: ID, t: T): E[Iterable[T]]
}
