package ch.ethz.inf.da.tipstersearch.scoring

import ch.ethz.inf.da.tipstersearch.CollectionStatistics

/**
  * The basic term frequency inverse document frequency model
  * This model uses the log_2 of the term frequencies
  * 
  * @constructor creates the tfidf model for given collection statistics
  * @param cs the collection statistics
  */
class TfidfModel(cs:CollectionStatistics) extends RelevanceModel {

    /**
      * Computes the score of given document and given query
      * 
      * @param queryTokens the list of tokens in the query
      * @param documentTokens the list of tokens in the document
      * @return the tfidf score
      */
    override def score(queryTokens:List[String], documentTokens:List[String]) : Double = {

        // Compute log tf
        val docLength:Int = documentTokens.length
        val logtf:Map[String, Double] = tf(documentTokens).map{
            case (k,0) => (k, 0.0) // 0 if tf = 0
            case (k,v) => (k, 1.0 + log2(v.toDouble)) // 1+log_2(tf) if tf > 0
        }

        // Compute tfidf for each query term
        queryTokens.map( q => logtf.getOrElse[Double](q, 0.0) * idf(q) ).sum
    }

    /**
      * Computes the inverse document frequency of a term
      */
    def idf(t:String) : Double = {
        1.0 + log2(cs.nrOfDocuments / (1.0 + cs.getDocumentFrequency(t)))
    }

}
