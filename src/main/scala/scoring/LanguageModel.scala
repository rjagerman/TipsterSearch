package ch.ethz.inf.da.tipstersearch.scoring

import scala.collection.mutable.HashMap
import ch.ethz.inf.da.tipstersearch.CollectionStatistics

/**
  * A language model used for scoring documents based on a query
  * This model uses jelinek-mercer smoothing
  * 
  * @constructor creates the language model for given collection statistics
  * @param cs the complete collection statistics
  */
class LanguageModel(cs:CollectionStatistics) extends RelevanceModel {

    /**
      * Computes the score of given document and given query
      * 
      * @param queryTokens the list of tokens in the query
      * @param documentTokens the list of tokens in the document
      * @return the jelinek mercer smoothed score
      */
    override def score(queryTokens:List[String], documentTokens:List[String]) : Double = {

        // Use a standard lambda value of 0.1
        val lambda:Double = 0.1

        queryTokens.map(
                w => log2(1.0 +
                  ((1.0-lambda) * p(w, documentTokens)) /
                  (lambda * p(w)))
            ).product

    }

    /**
      * Computes the probability of a word given a document
      * 
      * @param word the word
      * @param documentTokens the list of tokens in the document
      * @return the probability of given word in the document
      */
    def p(word:String, documentTokens:List[String]) : Double = {
        tf(documentTokens).getOrElse[Int](word, 0).toDouble / (documentTokens.length.toDouble)
    }

    /**
      * Computes the probability of a word over the entire collection
      * 
      * @param word the word
      * @return the probability of given word based on the collection
      */
    def p(word:String) : Double = {
        (cs.getCollectionFrequency(word).toDouble+1.0) / (cs.collectionLength.toDouble+1.0)
    }

}
