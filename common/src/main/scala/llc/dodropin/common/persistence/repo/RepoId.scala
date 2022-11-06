package llc.dodropin.common.persistence.repo

trait RepoId[ID] {
  def id: ID
}
