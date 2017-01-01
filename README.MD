## Problem description

Tieu owns a pizza restaurant and he manages it in his own way. While in a normal restaurant, a
customer is served by following the first-come, first-served rule, Tieu simply minimizes the average
waiting time of his customers. So he gets to decide who is served first, regardless of how sooner or later
a person comes.
Different kinds of pizzas take different amounts of time to cook. Also, once he starts cooking a pizza, he
cannot cook another pizza until the first pizza is completely cooked. Let's say we have three customers
who come at time t=0, t=1, & t=2 respectively, and the time needed to cook their pizzas is 3, 9, & 6
respectively. If Tieu applies first-come, first-served rule, then the waiting time of three customers is 3,
11, & 16 respectively. The average waiting time in this case is (3 + 11 + 16) / 3 = 10. This is not an
optimized solution. After serving the first customer at time t=3, Tieu can choose to serve the third
customer. In that case, the waiting time will be 3, 7, & 17 respectively. Hence the average waiting time
is (3 + 7 + 17) / 3 = 9.
Help Tieu achieve the minimum average waiting time. For the sake of simplicity, just find the integer part
of the minimum average waiting time.

For detailed description see Scala Code Challenge.pdf file in project directory.

### how to run
1. make sure you have apache maven and java 8 installed. 
2. `cd MinAvgWaitingTime` directory, `mvn clean package`
3. run application with `java -jar  ./target/mawt-1.0-jar-with-dependencies.jar`
 
By default, two jar are built: one without any deps, and one with everything required to run. 


#### reminder
Let's: do not talk about particular time measures, use "time quant" (TQ) instead.

Constraints:
1. !!! no information on future orders! => ORDERS ARE ALWAYS ORDERED IN TIME !!! every decision point requires recalculation,
2. No promise that customers can't order at the same time moment. 
3. No promise that there is no gaps between orders
4. No promise that there is at least one order
6. No other time expenses exists, except the ones for cooking.
7. cooking is not discrete process.


Degenerated cases: 
- No customers => MAWT == 0 or not available??? 
- N customers at 0 TQ => Sort by ???, sum up, div. 

- 1 customer => see next
- 1 customer at N-th TQ =>  MAWT == order prepare time, 0 to Nth distance must not be taken into account

=======================================================================================================================
No foresight...

    2nd(x tq) 3 rd(xx tq) 
     |          |
 |------------------| ---------------------
 0   cooking 1st    N 
         |
     we are here. We know about 2nd. It must be accounted. Cook, what's the plan? => to prepare 2nd. 
	 When we are at N, Cook, what's the plan? => to prepare ??? we have a choice between 2 and 3. 
	 
So it's totally depends on the moment in time when decision is made. Hence, available order pool is narrowed by `received` <= `current`
That's means that Order DOES NOT exists in storage UNTIL it is made. If it's there, it can be prepared. Hence, orders must be added to storage in a right time?

case class Order (when: Time, duration: TimeUnits)

=======================================================================================================================
Picking order

N orders, all available at once. Duration is incremental from 1 to N accordingly. 
1st => 1st.dur
2nd => 1st.dur + 2nd.dur
Nth => sum(1st.dur to Nth.dur) => 

1.st is a part of any waiting time. And the second of all except 1st and so on...
If 1st served first =>  total addition ==  1 * N
if Nth served first => total addition == N*N
1 * N < N * N => smallest durations must be served first. 

Looks like a truth. 
========================================================================================================================
Entities: 
1. Order => case class Order (when: Time, duration: TimeUnits)
2. WholeOrderList => application params Queue[Orders]
3. CurrentTimePointer: Int (why not as we operate on TQ), increments by picked order duration
3. AvailableOrders => Let's say there is N suitable orders. Let's put them in a Seq*. Now we have to pick them one by one 
in a right order. Options: to sort, O(n log n), to hold invariant "next is lowest" => heaps O(n)


readOrders
SortBy smallest when
Make PriorityQueue with smallest duration.
while (wholeList.nonEmpty || currentList.nonEmpty) 
    while (wholeList.nonEmpty && (wholeList.head <= currentTimePointer || currentList.isEmpty)){
        add head to currentList
        remove head from whole
        replace currentTimePointer with head.when unless head.when smaller                      
    }
    inc currentTimePointer by whole // pizza is ready  
    inc total by head.duration 
    dec total by head.when // substract the time customer was somewhere else 
}
