package ch.ethz.inf.da.tipstersearch

import scala.io.Source
import ch.ethz.inf.da.tipstersearch.io.QueryReader
import ch.ethz.inf.da.tipstersearch.io.DocumentStream
import ch.ethz.inf.da.tipstersearch.parsing.DocumentParser
import ch.ethz.inf.da.tipstersearch.parsing.Tokenizer
import ch.ethz.inf.da.tipstersearch.relevancemodels.TermFrequencyModel

case class Config(
    n: Int = 100,
    tipsterDirectory: String = "dataset/tipster",
    topicsFile: String = "dataset/topics",
    qrelsFile: String = "dataset/qrels"
)

object Main {

    def main(args:Array[String]) {

        val parser = new scopt.OptionParser[Config]("tipstersearch") {
            head("TipsterSearch", "0.1")
            opt[Int]('n', "n") action { (x, c) => c.copy(n = x) } text("The number of results to return per query (default: 100)")
            opt[String]('d', "tipsterDirectory") action { (x, c) => c.copy(tipsterDirectory = x) } text("The directory where the tipster zips are placed (default: 'dataset/tipster')")
            opt[String]('t', "topicsFile") action { (x, c) => c.copy(topicsFile = x) } text("The topics file (default: 'dataset/topics')")
            opt[String]('q', "qrelsFile") action { (x, c) => c.copy(qrelsFile = x) } text("The qrels file (default: 'dataset/qrels')")
        }

        parser.parse(args, Config()) map { config => 
            runSearch(config)
        }

    }

    def runSearch(config:Config) {

        // Read and preprocess queries
        val qr = new QueryReader()
        val queries = qr.read(config.topicsFile).map{ case (id,str) => (id, Tokenizer.tokenize(str).flatMap(x => x.toLowerCase.split("-"))) }

        // Open document stream
        val ds = new DocumentStream()
        val dp = new DocumentParser()
        var count = 0

        // Generate a buffered stream that reads multiple documents per iteration
        val bufferedStream = ds.readDirectory(config.tipsterDirectory)
                                .filter(x => !x.isEmpty())
                                .grouped(1000)

        // Generate a score stream that computes scores in parallel on the buffered entries
        val scoreStream = bufferedStream.flatMap(x => x.par
                            .map(dp.parse)
                            .map{case (id,str) => (id, Tokenizer.tokenize(str))}
                            .map{case (id,ls) => (id, queries.map{case (qid,qtokens) => (qid, TermFrequencyModel.score(qtokens, ls))})}
                            .toList
                          )

        // Iterate over the scores for each document
        for( (id:String, scores:List[(Int,Double)]) <- scoreStream ) {
            count += 1
            if(count % 1000 == 0) {
                println("Processed " + count + " documents!")
            }
            if(count >= 10000) {
                return
            }
        }

    }

}

