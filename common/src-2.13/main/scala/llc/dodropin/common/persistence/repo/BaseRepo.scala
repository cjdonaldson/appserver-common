package llc.dodropin.common.persistence.repo

/** BaseRepo providing CRUD contract in an Effect for an Entity T
  *
  * @param ID
  *   the type for the id to use: Guid, Int, String
  * @param T
  *   the type to be presented
  * @param E
  *   the effect system: Future, IO, ...
  */
trait BaseRepo[ID, T <: RepoId[ID], E[_]] extends BaseRepoSyntax[T, E] {

  log.info("repo init for {}", repoName)

  def repoName: String

  def getAll: E[Iterable[T]]

  def get(id: ID): E[T]

  def add(t: T): E[T]

  def update(t: T): E[T]

  def remove(id: ID): E[T]

}
