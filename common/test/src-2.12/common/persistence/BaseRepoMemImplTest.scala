package llc.dodropin.common.persistence.repo

import org.scalatest.funspec.AsyncFunSpec
import scala.concurrent.Future

case class TestItem(val id: String, value: Int) extends RepoId[String]

class BaseRepoMemImplTest extends AsyncFunSpec {
  import BaseRepoMemImplTest._

  class testRepo extends BaseRepoMemImpl[String, TestItem, Future]("test") with BaseRepoSyntaxFuture[TestItem]

  def getTestRepo: BaseRepo[String, TestItem, Future] = new testRepo

  describe("BaseRepo") {
    it("should start empty") {
      val testRepo = getTestRepo
      testRepo.getAll.map(_.toList).map(r => assert(r.isEmpty))
    }

    it("should add one user") {
      val testRepo = getTestRepo
      testRepo.add(item1).map(r => assert(r.equals(item1)))
      testRepo.getAll.map(_.toList).map { users =>
        assert(users.nonEmpty)
        assert(users.head.equals(item1))
      }
    }

    it("should add two users") {
      val testRepo = getTestRepo
      testRepo.add(item1).map(r => assert(r.equals(item1)))
      testRepo.add(item2).map(r => assert(r.equals(item2)))
      testRepo.getAll.map(_.toList).map { users =>
        assert(users.nonEmpty)
        assert(users.find(_ == item1).isDefined)
        assert(users.find(_ == item2).isDefined)
      }
    }

    it("should remove one user") {
      val testRepo = getTestRepo
      testRepo.add(item1).map(r => assert(r.equals(item1)))
      testRepo.getAll.map(_.toList).map { users =>
        assert(users.nonEmpty)
        assert(users.iterator.take(1).equals(item1))
      }
      testRepo.remove(item1.id).map { r =>
        assert(r.equals(item1))
      }
      testRepo.getAll.map(_.toList).map { users =>
        assert(users.isEmpty)
      }
    }

    it("should remove one of two user") {
      val testRepo = getTestRepo
      testRepo.add(item1).map(r => assert(r.equals(item1)))
      testRepo.add(item2).map(r => assert(r.equals(item2)))
      testRepo.getAll.map(_.toList).map { users =>
        assert(users.length == 2)
      }
      testRepo.remove(item1.id).map { r =>
        assert(r.equals(item1))
      }
      testRepo.getAll.map(_.toList).map { users =>
        assert(users.length == 1)
        assert(users.head.equals(item2))
      }
    }
  }
}

object BaseRepoMemImplTest {
  val item1 = TestItem("id1", 1)
  val item2 = TestItem("id2", 2)
}
