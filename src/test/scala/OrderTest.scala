import Main.Order
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpecLike, Matchers}

import scala.collection.mutable

@RunWith(classOf[JUnitRunner])
class OrderTest extends FunSpecLike with Matchers {

  // promise is that stream is always correct
  describe("Order must parse input stream to ordered collection of Orders") {

    it("build correct Order objects from stream") {
      val xs = Seq((0L, 3L), (1L, 9L), (2L, 6L))
      val result = Order.readOrders(xs.length, xs.toStream)
      result should contain theSameElementsAs Seq(Order(0, 3), Order(1, 9), Order(2, 6))
    }

    it("provided sorter must sort orders by arrival") {
      val xsUnordered = Seq((2L, 567566L), (0L, 509993L), (1L, 29L))
      val result1 = Order.readOrders(xsUnordered.length, xsUnordered.toStream)
      result1 should contain theSameElementsInOrderAs  Seq(Order(0, 509993), Order(1, 29), Order(2, 567566))

      val xsOrdered = Seq((0L, 3L), (1L, 9L), (2L, 6L))
      val result2 = Order.readOrders(xsOrdered.length, xsOrdered.toStream)
      result2 should contain theSameElementsAs Seq(Order(0, 3), Order(1, 9), Order(2, 6))
    }

    it("must return empty collection on zero orders"){
      val xs = Seq[(Long, Long)]()
      Order.readOrders(0, xs.toStream) shouldBe mutable.Buffer[Order]()
    }
  }
}
