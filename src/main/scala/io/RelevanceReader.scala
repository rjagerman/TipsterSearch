package ch.ethz.inf.da.tipstersearch.io

import ch.ethz.inf.da.tipstersearch.Query
import scala.io.Source

/**
  * Reads binary relevance truth values from the tipster dataset
  */
object RelevanceReader{

    /**
      * Reads binary relevance truth values from given file and stores
      * them in their appropriate query objects
      * 
      * @param path the path of the file to read from
      * @param queries the list of queries to store the values in
      */
    def read(path:String, queries:List[Query]) {
        for(query <- queries) {
            query.truth = Source.fromFile(path).getLines
                .filter(line => line.startsWith("" + query.id))
                .map(line => (line.replaceAll("^[0-9]+ 0 ([^ ]+).*", "$1"), line.replaceAll(".*([01])$", "$1").toInt) )
                .toList
        }
    }

}

