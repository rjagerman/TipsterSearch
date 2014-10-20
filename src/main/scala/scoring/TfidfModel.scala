package ch.ethz.inf.da.tipstersearch.scoring

import ch.ethz.inf.da.tipstersearch.CollectionStatistics

/**
  * The basic term frequency inverse document frequency model
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

        // Compute sqrt tf
        val docLength:Int = documentTokens.length
        val sqrttf:Map[String, Double] = tf(documentTokens).map{
            case (k,v) => (k, scala.math.sqrt(v.toDouble))
        }

        // Compute tfidf for each query term
        queryTokens.map( q => sqrttf.getOrElse[Double](q, 0.0) * idf(q) ).sum
    }

    /**
      * Computes the inverse document frequency of a term
      */
    def idf(t:String) : Double = {
        1.0 + log2(cs.nrOfDocuments / (1.0 + cs.getDocumentFrequency(t)))
    }

}
