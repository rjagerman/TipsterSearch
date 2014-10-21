package ch.ethz.inf.da.tipstersearch

import scala.collection.mutable.{Map, HashMap}
import ch.ethz.inf.da.tipstersearch.util.{Stopwatch, LinePrinter}

/**
  * Stores statistics about the complete document collection such as document frequencies,
  * longest document length, etc.
  */
class CollectionStatistics {

    var documentFrequencies:Map[String, Int] = new HashMap[String, Int]()
    var collectionFrequencies:Map[String, Int] = new HashMap[String, Int]()

    var maxDocLength:Int = 1
    var collectionLength:Int = 0
    var nrOfDocuments:Int = 0
    var uniqueTerms:Int = 0

    /**
      * Compute the collection statistics based on given iterator over documents
      * 
      * @param documents an iterator over all documents
      */
    def compute(documents:Iterator[Document]) {

        nrOfDocuments = 0
        collectionLength = 0

        var lastCount = 0
        val updateFrequency = 1000 // Update progress every this many milliseconds
        val globalStopwatch = new Stopwatch()
        val localStopwatch = new Stopwatch()

        for(doc <- documents) {
            nrOfDocuments += 1
            maxDocLength = scala.math.max(maxDocLength, doc.tokens.length)
            collectionLength += doc.tokens.length
            doc.tokens.groupBy(identity).mapValues(l => l.length).foreach {
                case (str,count) =>
                    documentFrequencies.put(str, documentFrequencies.getOrElse(str,0) + 1)
                    collectionFrequencies.put(str, collectionFrequencies.getOrElse(str,0) + count)
            }

            if(localStopwatch.milliseconds >= updateFrequency) {
                LinePrinter.print("Processed " + nrOfDocuments + " documents (" + (nrOfDocuments/(globalStopwatch.seconds+1)) + "/s) containing " + documentFrequencies.size + " unique terms (" + globalStopwatch + ")")
                lastCount = nrOfDocuments
                localStopwatch.start
            }
        }

        uniqueTerms = documentFrequencies.size

        LinePrinter.println("Done processing " + nrOfDocuments + " documents in " + globalStopwatch)

    }

    /**
      * @param str the word to obtain the document frequency for
      * @return the document frequency
      */
    def getDocumentFrequency(str:String) : Int = documentFrequencies.getOrElse(str, 0)

    /**
      * @param str the word to obtain the collection frequency for
      * @return the collection frequency
      */
    def getCollectionFrequency(str:String) : Int = collectionFrequencies.getOrElse(str, 0)
    
}

