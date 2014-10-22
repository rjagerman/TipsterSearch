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

    val rankings:List[String] = query.results.ordered.map(r => r.id)
    val retrieved:Set[String] = rankings.toSet
    val relevant:Set[String] = query.truth.filter(_._2 == 1).map{case (id,c) => id}.toSet

    /**
      * Computes the precision over the results
      * 
      * @return the precision
      */
    def precision : Double = {
        return (retrieved & relevant).size.toDouble / retrieved.size.toDouble
    }

    /**
      * Computes the precision over the first k results
      * 
      * @param k the number of results to consider
      * @return the precision
      */
    def precision(k:Int) : Double = {
        return (rankings.take(k).toSet & relevant).size.toDouble / k
    }

    /**
      * Computes the recall over the results
      * 
      * @return the recall
      */
    def recall : Double = {
        return (retrieved & relevant).size.toDouble / relevant.size.toDouble
    }

    /**
      * Indicator function to determine if document at index k is relevant
      * 
      * @param k the index to check (1-based)
      * @return 1 if the document at k is relevant, 0 if it is not
      */
    def isRelevant(k:Int) : Int = {
        if (relevant.contains(rankings(k-1))) return 1 else return 0
    }

    /**
      * Computes the average precision over the results
      * 
      * @return the average precision
      */
    def averagePrecision : Double = {
        var average:Double = 0.0
        (1 to retrieved.size) foreach {
            k => average += precision(k) * isRelevant(k)
        }
        val nrOfRelevantDocuments = (retrieved & relevant).size

        // Prevent division by 0 if none of the documents were relevant...
        if(nrOfRelevantDocuments == 0) 0
        else average / nrOfRelevantDocuments.toDouble
    }

}

