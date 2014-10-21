package ch.ethz.inf.da.tipstersearch

import ch.ethz.inf.da.tipstersearch.scoring.{TopResults, Result, RelevanceModel}
import ch.ethz.inf.da.tipstersearch.util.{Stopwatch, LinePrinter}

/** 
  * A search engine that uses a Relevance Model to rank documents
  *
  * @constructor creates a search engine that uses given relevance model
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

        // For each document: score it per query and add it to each query's results
        var iter = 0
        var lastCount = 0
        val updateFrequency = 1000 // Update progress every this many milliseconds
        val localStopwatch = new Stopwatch()
        val globalStopwatch = new Stopwatch()
        for (document <- documents) {

            queries.par.foreach{
                query => query.results.add(new Result(document.name, model.score(query.tokens, document.tokens)))
            }

            iter += 1
            if (localStopwatch.milliseconds >= updateFrequency) {
                LinePrinter.print("Searched " + iter + " documents (" + (iter/(globalStopwatch.seconds+1)) + "/s) (" + globalStopwatch + ")")
                lastCount = iter
                localStopwatch.start
            }
            
        }

        LinePrinter.println("Done searching " + iter + " documents in " + globalStopwatch)

    }
}
