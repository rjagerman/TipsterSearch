package ch.ethz.inf.da.tipstersearch.scoring

import ch.ethz.inf.da.tipstersearch.CollectionStatistics

class TfidfModel(cs:CollectionStatistics) extends RelevanceModel {

    override def score(queryTokens:List[String], documentTokens:List[String]) : Double = {

        // Compute log tf
        val docLength:Int = documentTokens.length
        val logtf:Map[String, Double] = tf(documentTokens).map{
            case (k,v) => (k, log2(v.toDouble/docLength+1.0))
        }

        // Compute tfidf for each query term
        queryTokens.map( q => logtf.getOrElse[Double](q, 0.0) * idf(q) ).sum
    }

    def idf(t:String) : Double = {
    	log2(cs.nrOfDocuments / (1.0 + cs.getDocumentFrequency(t)))
    }

}
