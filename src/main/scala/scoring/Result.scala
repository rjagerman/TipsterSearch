package ch.ethz.inf.da.tipstersearch.scoring

class Result(val id: String, val score: Double) {
    override def toString() : String = {
        return id + " [" + score + "]"
    }
}

