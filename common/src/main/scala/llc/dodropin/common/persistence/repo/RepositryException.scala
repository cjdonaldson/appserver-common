package llc.dodropin.common.persistence.repo

class RepositoryException[ID](repoName: String, id: Option[ID], message: String) extends Exception(message)

object RepositoryException {
  def apply(repoName: String, message: String): RepositoryException[Any] = new RepositoryException(repoName, None, message)

  def apply[ID](repoName: String, id: ID, message: String): RepositoryException[ID] = new RepositoryException(repoName, Some(id), message)

  def idExists[ID](repoName: String, id: ID): RepositoryException[ID] = new RepositoryException(repoName, Some(id), s"$id exists")
}
