package llc.dodropin.common

import org.scalatest.funspec.AnyFunSpec
import java.util.UUID

class GuidTest extends AnyFunSpec {
  describe("Guid") {
    it("should create 2 unique id") {
      val guid1 = Guid.create
      val guid2 = Guid.create
      assert(!guid1.equals(guid2))
    }

    it("should create a large numbers of unique ids") {
      val count = 1000
      assert(
        1.to(count)
          .map(_ => Guid.create)
          .toSet
          .size == count
      )
    }

    // val count = 1000
    // it(s"should create a large number ($count) of unique 8 character length ids (really 48 bits due to base64)") {
    //   // brittle by nature as it is a fractional part of a unique value
    //   assert(
    //     1.to(count)
    //       .map(_ => Guid.createOfLength(8))
    //       .toSet
    //       .size == count
    //   )
    // }

    // it(s"should create a large number ($count) of unique 6 character length ids (really 36 bits due to base64)") {
    //   // brittle by nature as it is a fractional part of a unique value
    //   assert(
    //     1.to(count)
    //       .map(_ => Guid.createOfLength(6))
    //       .toSet
    //       .size == count
    //   )
    // }
  }

  describe("Guid UUID serdes") {
    val uuidStr = "94051272-1fb9-3ea8-be4f-c8e65ffcd172"
    val guidStr = "vk_I5l_80XKUBRJyH7k-qA"

    it(s"should extract a UUID from a Guid") {
      val guid = Guid(guidStr)
      assert(Guid.toUuid(guid).toString == uuidStr)
    }

    it(s"should create a Guid from a UUID") {
      val uuid = UUID.fromString(uuidStr)
      assert(Guid.from(uuid).value == guidStr)
    }

  }

}
