package ch.ethz.inf.da.tipstersearch

import scala.pickling._
import binary._
import java.io.{File, BufferedInputStream, FileInputStream, FileOutputStream, InputStream}
import scala.collection.mutable.HashMap
import ch.ethz.inf.da.tipstersearch.io.{QueryReader, RelevanceReader, ZipIterator}
import ch.ethz.inf.da.tipstersearch.scoring.{RelevanceModel, TfidfModel, LanguageModel}
import ch.ethz.inf.da.tipstersearch.processing.Tokenizer
import ch.ethz.inf.da.tipstersearch.util.Stopwatch
import ch.ethz.inf.da.tipstersearch.metrics.PrecisionRecall

/** Defines the command line options 
  */
case class Config(
    n: Int = 100,
    tipsterDirectory: String = "dataset/tipster",
    topicsFile: String = "dataset/topics",
    qrelsFile: String = "dataset/qrels"
)

/** Entry point of the application
  */
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
            run(config)
        }

    }

    /** runs the application with the options specified in the config
      *
      * @param config the configuration to use
      */
    def run(config:Config) {

        // Start timer
        Stopwatch.start

        // Read queries and binary relevance truth values
        val queries:List[Query] = QueryReader.read(config.topicsFile)
        RelevanceReader.read(config.qrelsFile, queries)

        // Collect statistics about the document collection
        println("Computing document collection statistics")
        var cs:CollectionStatistics = null
        if (new File("dataset/collectionstatistics").exists) {
            println("Retrieving cached copy")
            cs = readCollectionStatisticsCache("dataset/collectionstatistics")
        } else {
            cs = new CollectionStatistics()
            cs.compute(documentIterator(config.tipsterDirectory))
            writeCollectionStatisticsCache(cs, "dataset/collectionstatistics")
        }
        
        // Set up the relevance model to use
        val model:RelevanceModel = new TfidfModel(cs)

        // Search for the queries
        println("Running search")
        val searchEngine:SearchEngine = new SearchEngine(model)
        searchEngine.search(queries, documentIterator(config.tipsterDirectory), config.n)

        // Display metrics
        var MAP:Double = 0.0
        for( query <- queries ) {
            val pr = new PrecisionRecall(query)
            MAP += pr.precision
            println(query.id + " ('" + query + "')")
            println("   Precision: %.3f".format(pr.precision))
            println("   Recall: %.3f".format(pr.recall))
            println("   Avg Precision: %.3f".format(pr.averagePrecision))
            
            var count = 0
            for(r <- query.results.ordered) {
                count += 1
                println(query.id + " " + count + " " + r.id)
            }
        }
        MAP /= queries.size.toDouble
        println("MAP: " + MAP)

        // Print the time spent
        print("Total time: ")
        Stopwatch.printTime

    }

    /** Returns an iterator over the tipster documents found in given directory
      * 
      * @param directory the directory to search in
      * @return an iterator over all documents
      */
    def documentIterator(directory:String) : Iterator[Document] = {
        new File(directory).listFiles.iterator
            .filter(f => f.getName.endsWith(".zip"))
            .flatMap(f =>
                new ZipIterator(new FileInputStream(f.getAbsolutePath)).map{
                    case (name:String, is:InputStream) => new Document(is)
                }
            )
    }

    /**
      * Write the collection statistics to a cache file for future use
      * 
      * @param cs the collection statistics
      * @param file the file to store it in
      */
    def writeCollectionStatisticsCache(cs:CollectionStatistics, file:String) {
        val fos:FileOutputStream = new FileOutputStream(file)
        fos.write(cs.pickle.value)
        fos.close()
    }

    /**
      * Reads the collection statistics from given cache file
      * 
      * @param file the file to read from
      * @return the collection statistics
      */
    def readCollectionStatisticsCache(file:String) : CollectionStatistics = {
        val fis:BufferedInputStream = new BufferedInputStream(new FileInputStream(file))
        val input:BinaryPickleStream = BinaryPickleStream(fis)
        input.unpickle[CollectionStatistics]
    }

}

