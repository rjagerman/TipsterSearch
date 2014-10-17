package ch.ethz.inf.da.tipstersearch.relevancemodels

class TermFrequencyModel {
    def score(queryTokens:List[String], documentTokens:List[String]) : Double = {
        val dtf = logtf(tf(documentTokens))
        queryTokens.flatMap(q => dtf.get(q)).sum
    }

    def log2(x: Double) = scala.math.log(x)/scala.math.log(2)

    def tf(tokens:List[String]) : Map[String, Int] = {
        tokens.groupBy(identity).mapValues(l => l.length)
    }

    def logtf(tf : Map[String, Int]) = {
        tf.mapValues(v => log2(v.toDouble/tf.values.sum+1.0))
    }
}
