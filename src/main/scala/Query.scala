package ch.ethz.inf.da.tipstersearch

import ch.ethz.inf.da.tipstersearch.processing.TextProcessor
import ch.ethz.inf.da.tipstersearch.scoring.TopResults

/** Represents a query 
  */
class Query(val id:Int, val query:String) {
	val tokens:List[String] = TextProcessor.process(query)
	var results:TopResults = null
	var truth:List[(String,Int)] = List[(String,Int)]()
	override def toString():String = query
}
