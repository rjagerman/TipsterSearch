package ch.ethz.inf.da.tipstersearch

import scala.collection.mutable.HashMap
import ch.ethz.inf.da.tipstersearch.scoring.{TopResults, Result, RelevanceModel}
import ch.ethz.inf.da.tipstersearch.util.Stopwatch

/** Creates a new search engine that uses given Relevance Model
  *
  * @param model the relevance model to use
  */
class SearchEngine(model:RelevanceModel) {

    /** Searches given queries on given documents and returns the top n results
      *
      * @param queries the queries to search on
      * @param documents the documents to search in
      * @param n the amount of results to retain
      */
    def search(queries:List[Query], documents:Iterator[Document], n:Int) {

        // Prepare a container for each query to store the results in
        for( query <- queries ) {
            query.results = new TopResults(n)
        }

        // For each document process it and evaluate it with the queries
        var iter = 0
        var lastCount = 0
        val sw = new Stopwatch()
        for (document <- documents) {

            queries.par.foreach{
                query => query.results.add(new Result(document.name, model.score(query.tokens, document.tokens)))
            }

            iter += 1
            if (sw.seconds >= 1) {
                print("\rSearched " + iter + " documents (" + (iter - lastCount) + "/s)  ")
                lastCount = iter
                sw.start
            }
            
        }

    }
}
