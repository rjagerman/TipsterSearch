package ch.ethz.inf.da.tipstersearch

import scala.collection.immutable.TreeMap
import ch.ethz.inf.da.tipstersearch.util.Stopwatch

/**
  * Stores statistics about the complete document collection such as document frequencies,
  * longest document length, etc.
  * 
  * Due to the size of the document collection, this uses feature hashing (Murmur3) for
  * storage and lookup of words. It uses a large size (2^25) to prevent collisions.
  */
class CollectionStatistics {

    var size:Int = 25

    var documentFrequencies:Array[Int] = new Array[Int](1 << size)
    var collectionFrequencies:Array[Int] = new Array[Int](1 << size)

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
                    val i:Int = index(str)
                    if(documentFrequencies(i) == 0)
                        uniqueTerms += 1
                    documentFrequencies(i) = documentFrequencies(i) + 1
                    collectionFrequencies(i) = collectionFrequencies(i) + count
            }

            if(localStopwatch.milliseconds >= updateFrequency) {
                print("\033[2K") // Clear line
                print("\rProcessed " + nrOfDocuments + " documents (" + (nrOfDocuments/(globalStopwatch.seconds+1)) + "/s) containing " + uniqueTerms + " unique terms (" + globalStopwatch + ")")
                lastCount = nrOfDocuments
                localStopwatch.start
            }
        }

        print("\033[2K") // Clear line
        println("\rDone processing " + nrOfDocuments + " documents in " + globalStopwatch)

    }

    /**
      * @param str the word to obtain the document frequency for
      * @return the document frequency
      */
    def getDocumentFrequency(str:String) : Int = documentFrequencies(index(str))

    /**
      * @param str the word to obtain the collection frequency for
      * @return the collection frequency
      */
    def getCollectionFrequency(str:String) : Int = collectionFrequencies(index(str))

    /**
      * Obtains the Murmur3 hashed index which is very fast and 
      * has nice collision-prevention properties for short text
      * 
      * @param str the string to get the index for
      * @return int the hashed integer index
      */
    private def index(str:String) : Int = (scala.util.hashing.MurmurHash3.stringHash(str)) >>> (32-size)
    
}

