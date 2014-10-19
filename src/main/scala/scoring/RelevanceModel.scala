package ch.ethz.inf.da.tipstersearch.scoring

abstract class RelevanceModel {

	def score(queryTokens:List[String], documentTokens:List[String]) : Double

	def log2(x: Double) = scala.math.log(x)/scala.math.log(2)

	def tf(tokens:List[String]) : Map[String, Int] = tokens.groupBy(identity).mapValues(l => l.length)

}
