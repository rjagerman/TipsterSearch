package ch.ethz.inf.da.tipstersearch.scoring

import scala.collection.mutable.PriorityQueue

class TopResults(n: Int) extends PriorityQueue[Result]()(Ordering.by(res => -res.score)) {

    //private def score (res: Result) = -res.score 
    //private val heap = new PriorityQueue[Result]()(Ordering.by(score))

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

    def ordered = this.toList.sortBy(res => -res.score)  

    /*// score a document and try to add to results
    def process(title: String, doc: List[String]) : Boolean = {
        val score = query.score(doc)
        add(Result(title,score))
    }
    
    // get top n results (or m<n, if not enough docs processed)
    def results = heap.toList.sortBy(res => -res.score)    

        // heap and operations on heap
    
    private def add(res: ScoredResult) : Boolean = {    
        if (heap.size < n)  { // heap not full
            heap += res
            true
        } else if (heap.head.score < res.score) {
            heap.dequeue
            heap += res
            true
        } else false
    }*/

}

