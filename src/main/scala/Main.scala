package ch.ethz.inf.da.tipstersearch

import scala.io.Source
import ch.ethz.inf.da.tipstersearch.io.QueryReader
import ch.ethz.inf.da.tipstersearch.io.DocumentStream
import ch.ethz.inf.da.tipstersearch.parsing.DocumentParser
import ch.ethz.inf.da.tipstersearch.parsing.TextParser
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

        // Create parsers
        val dp = new DocumentParser()
        val tp = new TextParser()

        // Read and preprocess queries
        val qr = new QueryReader()
        val queries = qr.read(config.topicsFile).map{ case (id,str) => (id, tp.parse(str).flatMap(x => x.toLowerCase.split("-"))) }

        // Open document stream
        val ds = new DocumentStream()
        var count = 0

        val tfm = new TermFrequencyModel()
        def score(a:List[String],b:List[String]) : Double = tfm.score(a,b)

        // Iterate over the documents, ranking each one
        for(doc:String <- ds.readDirectory(config.tipsterDirectory)) {

            val (docId, contents) = dp.parse(doc) 
            val docTokens = tp.parse(contents)

            for((queryId,queryTokens) <- queries) {
                val s = score(queryTokens, docTokens)
            }

            count += 1
            if(count % 1000 == 0) {
                println("Processed " + count + " documents!")
            }
        }

    }

}

