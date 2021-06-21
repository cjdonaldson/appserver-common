package llc.dodropin.persistence.users

// import java.time.Instant

// import llc.dodropin.common.Guid

// import slick.jdbc.MySQLProfile.api._
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

// trait DbConfiguration {
//   lazy val config = DatabaseConfig.forConfig[JdbcProfile]("db")
// }

// final case class UserRepoSlick()

trait Db {
  val config: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = config.db
}
