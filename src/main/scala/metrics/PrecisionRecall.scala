package ch.ethz.inf.da.tipstersearch.metrics

import ch.ethz.inf.da.tipstersearch.Query

/**
  * Computes basic IR retrieval metrics about a given query
  * 
  * The query contains the result set and the known binary relevance ground truth
  * which it uses to compute various IR retrieval metrics such as precision and recall
  * 
  * @constructor creates a precision recall metric based on given query data
  * @param the query
  */
class PrecisionRecall(query:Query) {

    val retrievedDocuments:Set[String] = query.results.ordered.map(r => r.id).toSet
    val relevantDocuments:Set[String] = query.truth.filter(_._2 == 1).map{case (id,c) => id}.toSet

    def precision : Double = {
        return (retrievedDocuments & relevantDocuments).size.toDouble / retrievedDocuments.size.toDouble
    }

    def precision(k:Int) : Double = {
        return (retrievedDocuments.take(k) & relevantDocuments).size.toDouble / retrievedDocuments.take(k).size.toDouble
    }

    def recall : Double = {
        return (retrievedDocuments & relevantDocuments).size.toDouble / relevantDocuments.size.toDouble
    }

    def averagePrecision : Double = {
        var average:Double = 0.0
        1 to retrievedDocuments.size foreach { k => average += precision(k) }
        average / retrievedDocuments.size.toDouble
    }

}

