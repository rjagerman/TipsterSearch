package ch.ethz.inf.da.tipstersearch

import scala.pickling._
import binary._
import java.io.{PrintWriter, File, BufferedInputStream, FileInputStream, FileOutputStream, InputStream}
import scala.collection.mutable.HashMap
import ch.ethz.inf.da.tipstersearch.io.{QueryReader, RelevanceReader, ZipIterator}
import ch.ethz.inf.da.tipstersearch.scoring.{RelevanceModel, TfidfModel, LanguageModel}
import ch.ethz.inf.da.tipstersearch.processing.Tokenizer
import ch.ethz.inf.da.tipstersearch.util.Stopwatch
import ch.ethz.inf.da.tipstersearch.metrics.PrecisionRecall

/**
  * Defines the command line options 
  */
case class Config(
    n: Int = 100,
    tipsterDirectory: String = "dataset/tipster",
    topicsFile: String = "dataset/topics",
    qrelsFile: String = "dataset/qrels",
    model: String = "tfidf"
)

/**
  * Main application object, execution starts here
  */
object Main {

    /**
      * Entry point of the application
      *
      * @param args The command line arguments
      */
    def main(args:Array[String]) {

        val parser = new scopt.OptionParser[Config]("tipstersearch") {
            head("TipsterSearch", "0.1")
            opt[Int]('n', "n") action { (x, c) => c.copy(n = x) } text("The number of results to return per query (default: 100)")
            opt[String]('d', "tipsterDirectory") action { (x, c) => c.copy(tipsterDirectory = x) } text("The directory where the tipster zips are placed (default: 'dataset/tipster')")
            opt[String]('t', "topicsFile") action { (x, c) => c.copy(topicsFile = x) } text("The topics file (default: 'dataset/topics')")
            opt[String]('q', "qrelsFile") action { (x, c) => c.copy(qrelsFile = x) } text("The qrels file (default: 'dataset/qrels')")
            opt[String]('m', "model") action { (x, c) => c.copy(model = x) } validate {
                    x => if(x == "tfidf" || x == "language") success else failure("Value <model> must be either 'tfidf' or 'language'")
                } text("The model to use, valid values: [language|tfidf] (default: 'tfidf')")
        }

        parser.parse(args, Config()) map { config => 
            run(config)
        }

    }

    /**
      * Runs the application with the options specified in the config.
      *
      * @param config the configuration to use
      */
    def run(config:Config) {

        // Start timer
        val stopwatch = new Stopwatch()

        // Read queries and binary relevance truth values
        val queries:List[Query] = QueryReader.read(config.topicsFile)
        RelevanceReader.read(config.qrelsFile, queries)

        // Collect statistics about the document collection
        // If possible, obtain the cached copy so we don't have to compute it again on each run
        println("Computing document collection statistics")
        var cs:CollectionStatistics = null
        if (new File("dataset/stat.cache").exists) {
            println("Retrieving cached copy")
            cs = readCollectionStatisticsCache("dataset/stat.cache")
        } else {
            cs = new CollectionStatistics()
            cs.compute(documentIterator(config.tipsterDirectory))
            writeCollectionStatisticsCache(cs, "dataset/stat.cache")
        }
        
        // Set up the relevance model to use, either TfidfModel or LanguageModel
        var model:RelevanceModel = null
        if(config.model == "tfidf") {
            println("Using tfidf model")
            model = new TfidfModel(cs)
        } else {
            println("Using language model")
            model = new LanguageModel(cs)
        }

        // Create the search engine with the chosen relevance model and run the search
        // This will take a long time
        println("Running search")
        val searchEngine:SearchEngine = new SearchEngine(model)
        searchEngine.search(queries, documentIterator(config.tipsterDirectory), config.n)

        // After search is complete, open the output file for the rankings
        var outputFile:File = null
        if(config.model == "tfidf") {
            outputFile = new File("ranking-t-rolf-jagerman.run")
        } else {
            outputFile = new File("ranking-l-rolf-jagerman.run")
        }
        val output = new PrintWriter(outputFile)

        // Compute performance with various metrics per query
        // This also writes the ranked results to a file
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
                output.println(query.id + " " + count + " " + r.id.replaceAll("[^a-zA-Z0-9]+", ""))
            }
        }
        output.flush()
        output.close()

        // Compute and display the global metric (MAP)
        MAP /= queries.size.toDouble
        println("MAP: %.3f".format(MAP))

        // Print the total time spent
        print("Total time: ")
        println(stopwatch)

    }

    /**
      * Returns an iterator over the tipster documents found in given directory
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

