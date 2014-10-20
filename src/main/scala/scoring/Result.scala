package ch.ethz.inf.da.tipstersearch.scoring

/**
  * A simpel result consisting of a document id and a score
  * 
  * @constructor creates a result object with given id and score
  * @param id the document id
  * @param score the score of the document
  */
class Result(val id: String, val score: Double) {
    override def toString() : String = {
        return id + " [" + score + "]"
    }
}

