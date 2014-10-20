package ch.ethz.inf.da.tipstersearch.scoring

import scala.collection.mutable.PriorityQueue

/**
  * Maintains the top n results
  * 
  * @constructor creates a container that holds the top n results
  * @param n the amount of results to retain
  */
class TopResults(n: Int) extends PriorityQueue[Result]()(Ordering.by(res => -res.score)) {

    /**
      * Adds a result to the top results if it is scored high enough
      * 
      * @param res the result to add
      * @return true if it was added, false if it was discarded
      */
    def add(res: Result) : Boolean = {
        if (size < n)  {
            // Always add if the heap is not full
            this += res
            true
        } else if (head.score < res.score) {
            // Only add if the score is better than the worst in the heap
            dequeue
            this += res
            true
        } else {
            false
        }
    }

    /**
      * Returns the top results ordered by score
      */
    def ordered = this.toList.sortBy(res => -res.score)  

}

