import java.util.Scanner

import scala.collection.mutable
import scala.io.StdIn

object Main {

  case class Order(when: Long, duration: Long)

  object Order {
    private val consoleScanner = new Scanner(Console.in)

    val shortestFirst = new Ordering[Order] {
      override def compare(x: Order, y: Order): Int = y.duration compare x.duration
    }

    /**
      * build order list from provided stream.
      * Stream introduced for testing purposes. Collection or no-param-func will do as well.
      * @param count how much orders to read
      * @param source stream of Tuple2[Long, Long]
      * @return ordered order list
      */
    def readOrders(count: Int, source: => Stream[(Long, Long)] = consoleStream): mutable.Buffer[Order] = {
      source.take(count)
        .map { case (w, d) => Order(w, d) }
        .toBuffer
        .sortWith(_.when < _.when)
    }

    def consoleStream: Stream[(Long, Long)] = Stream.cons((consoleScanner.nextInt, consoleScanner.nextInt), consoleStream)
  }

  class Cook {
    /**
      * Serves customer choosing smallest avarage wating time possible.
      * Provided orders MUST be sorted in
      *
      * @param knownOrders known orders placed in arrival order.
      * @return total time required
      */
    def serveEveryone(knownOrders: mutable.Buffer[Order]): Long = {
      val currentOrders = mutable.PriorityQueue[Order]()(Order.shortestFirst)
      // or even bigInt. Generally, everyone loves "what if overflow" questions.
      var currentTime = 0L
      var totalTime = 0L

      while (knownOrders.nonEmpty || currentOrders.nonEmpty) {
        while (knownOrders.nonEmpty && (knownOrders.head.when <= currentTime || currentOrders.isEmpty)) {
          val order = knownOrders.head
          currentOrders += order
          knownOrders remove 0
          // Consider the situation when there is (0->1), (2->4) orders. This introduces a gap which must be handled anyway.
          // declare new boundary
          currentTime = Math.max(currentTime, order.when)
        }

        val order = currentOrders.dequeue()
        currentTime += order.duration
        // customer may arriva after the current order was prepared or, for example, in the middle of the process
        totalTime += currentTime - order.when
      }
      totalTime
    }
  }

  def main(args: Array[String]) {
    val orderNum = StdIn.readInt()
    if (orderNum > 0)       {
      val knownOrders = Order.readOrders(orderNum)
      val cook = new Cook
      val totalTime = cook.serveEveryone(knownOrders)
      println(totalTime / orderNum)
    } else
      println("No customers, no waiting")
  }


}
