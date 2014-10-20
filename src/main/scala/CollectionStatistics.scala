package ch.ethz.inf.da.tipstersearch

import scala.collection.immutable.TreeMap
import ch.ethz.inf.da.tipstersearch.util.Stopwatch

/** Stores statistics about the complete document collection such as document frequencies,
  * longest document length, etc.
  * 
  * Due to the size of the document collection, this uses feature hashing (Murmur3) for
  * storage and lookup of words. It uses a large size (2^26) to prevent collisions.
  */
class CollectionStatistics {

    var size:Int = 25

    var documentFrequencies:Array[Int] = new Array[Int](1 << size)
    var collectionFrequencies:Array[Int] = new Array[Int](1 << size)

    var maxDocLength:Int = 1
    var collectionLength:Int = 0
    var nrOfDocuments:Int = 0
    var uniqueTerms:Int = 0

    def compute(documents:Iterator[Document]) {

        nrOfDocuments = 0
        collectionLength = 0

        var lastCount = 0
        val sw = new Stopwatch()
        sw.start

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

            if(sw.seconds >= 1) {
                sw.start
                print("\rProcessed " + nrOfDocuments + " documents containing " + uniqueTerms + " unique terms (" + (nrOfDocuments - lastCount) + "/s)  ")
                lastCount = nrOfDocuments
            }
        }

    }

    def getDocumentFrequency(str:String) : Int = documentFrequencies(index(str))
    def getCollectionFrequency(str:String) : Int = collectionFrequencies(index(str))
    def index(str:String) : Int = (scala.util.hashing.MurmurHash3.stringHash(str)) >>> (32-size)
    
}

