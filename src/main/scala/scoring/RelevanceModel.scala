package ch.ethz.inf.da.tipstersearch.scoring

/**
  * Abstract class representing a basic relevance model.
  * It provdides access to useful functions.
  */
abstract class RelevanceModel {

    /**
      * Override this function to compute the score of given document and given query
      * 
      * @param queryTokens the list of tokens in the query
      * @param documentTokens the list of tokens in the document
      * @return the document's score for this query
      */
    def score(queryTokens:List[String], documentTokens:List[String]) : Double

    /**
      * Computes the log_2
      * 
      * @param x the value to compute the log2 for
      */
    def log2(x: Double) = scala.math.log(x)/scala.math.log(2)

    /**
      * Computes the term frequencies of given tokens
      * 
      * @param tokens the tokens to compute the frequencies for
      */
    def tf(tokens:List[String]) : Map[String, Int] = tokens.groupBy(identity).mapValues(l => l.length)

}
