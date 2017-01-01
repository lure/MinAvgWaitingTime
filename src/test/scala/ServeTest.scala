import Main.{Cook, Order}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{FunSpecLike, Matchers}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

@RunWith(classOf[JUnitRunner])
class ServeTest extends FunSpecLike with Matchers {
  describe("serving customers"){
    val cook = new Cook

    it("must return 0 for empty order list"){
      cook.serveEveryone(ArrayBuffer.empty[Order]) shouldBe 0
    }

    it("must return order.duration for list with a single order independegly of arrival time"){
      cook.serveEveryone(mutable.Buffer(Order(0, 300))) shouldBe 300
      cook.serveEveryone(mutable.Buffer(Order(8, 300))) shouldBe 300
    }

    it("must substruct time gaps between orders from total time"){
      val xs = mutable.Buffer(Order(0, 100), Order(3, 10), Order(4, 15))
//      100
//      97 + 10
//      96 + 10 + 15
      cook.serveEveryone(xs) shouldBe  328
    }

    it("must count smallest average wating time even if customers made their orders at once"){
      val xs = mutable.Buffer(Order(0, 100), Order(0, 10), Order(0, 15), Order(0, 1))
      // 1, 11, 26, 126
      cook.serveEveryone(xs) shouldBe  164
    }

    it("handle int overflow. Int is chosen because it's most expected data type and most checks will be perfomred against it"){
      val xs = mutable.Buffer(Order(0, 2147483647L), Order(0, 100L))
      cook.serveEveryone(xs) shouldBe  2147483847L
    }
  }
}
