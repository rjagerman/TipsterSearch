package ch.ethz.inf.da.tipstersearch.scoring

import scala.collection.mutable.HashMap
import ch.ethz.inf.da.tipstersearch.CollectionStatistics

/**
  * A language model used for scoring documents based on a query
  * 
  * @param cs the complete collection statistics
  */
class LanguageModel(cs:CollectionStatistics) extends RelevanceModel {

    /**
      * Scores the given document using the query
      * 
      * @param queryTokens a list of tokens as they appear in the query
      * @param documentTokens a list of tokens as they appear in the document
      */
    override def score(queryTokens:List[String], documentTokens:List[String]) : Double = {
        val doctf = tf(documentTokens)
        val docLength:Int = documentTokens.length

        // Get a lambda value based on document length
        val lambda:Double = (1.0 - (docLength.toDouble / cs.maxDocLength.toDouble)*0.9)

        queryTokens.map(
                w => log2(1.0 + ((1.0-lambda)/lambda)*(p(w, doctf, docLength)/p(w))) + log2(lambda)
            ).reduce(_+_)

    }

    /**
      * Computes the probability of a word given a document
      * 
      * @param word the word
      * @param termFrequencies the term frequencies of the document
      * @param docLength the length of the document
      */
    def p(word:String, termFrequencies:Map[String, Int], docLength:Int) : Double = {
        termFrequencies.getOrElse[Int](word, 0) / (docLength+1.0)
    }

    /**
      * Computes the probability of a word over the entire collection
      * 
      * 
      */
    def p(word:String) : Double = {
        (cs.getCollectionFrequency(word) / cs.uniqueTerms)
    }

}
