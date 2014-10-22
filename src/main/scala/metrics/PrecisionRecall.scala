package ch.ethz.inf.da.tipstersearch.metrics

import ch.ethz.inf.da.tipstersearch.Query

/**
  * Computes basic IR retrieval metrics about a given query.
  * The query contains the result set and the known binary relevance ground truth which
  * this class uses to compute various IR retrieval metrics such as precision and recall.
  * 
  * @constructor creates a precision recall metric based on given query data
  * @param the query
  */
class PrecisionRecall(query:Query) {

    val retrievedDocuments:Set[String] = query.results.ordered.map(r => r.id).toSet
    val relevantDocuments:Set[String] = query.truth.filter(_._2 == 1).map{case (id,c) => id}.toSet

    /**
      * Computes the precision over the results
      * 
      * @return the precision
      */
    def precision : Double = {
        return (retrievedDocuments & relevantDocuments).size.toDouble / retrievedDocuments.size.toDouble
    }

    /**
      * Computes the precision over the first k results
      * 
      * @param k the number of results to consider
      * @return the precision
      */
    def precision(k:Int) : Double = {
        return (retrievedDocuments.take(k) & relevantDocuments).size.toDouble / retrievedDocuments.take(k).size.toDouble
    }

    /**
      * Computes the recall over the results
      * 
      * @return the recall
      */
    def recall : Double = {
        return (retrievedDocuments & relevantDocuments).size.toDouble / relevantDocuments.size.toDouble
    }

    /**
      * Computes the average precision over the results
      * 
      * @return the average precision
      */
    def averagePrecision : Double = {
        var average:Double = 0.0
        var prevPrecision = 0.0
        var count = 0
        1 to retrievedDocuments.size foreach {
            k => if(precision(k) > prevPrecision) {
                     average += precision(k)
                     count += 1
                 }
                 prevPrecision = precision(k)
        }
        if(count == 0) { return 0.0 }
        else { return average / count }
    }

}

