package ch.ethz.inf.da.tipstersearch

import ch.ethz.inf.da.tipstersearch.processing.TextProcessor
import ch.ethz.inf.da.tipstersearch.scoring.TopResults

/**
  * A simple query that also holds containers for results and truth values
  * 
  * @constructor creates a query with given id and query string
  * @param id The integer identifier of the query
  * @param query The query as a string
  */
class Query(val id:Int, val query:String) {
    val tokens:List[String] = TextProcessor.process(query)
    var results:TopResults = null
    var truth:List[(String,Int)] = List[(String,Int)]()
    override def toString():String = query
}
