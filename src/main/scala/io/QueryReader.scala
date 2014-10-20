package ch.ethz.inf.da.tipstersearch.io

import ch.ethz.inf.da.tipstersearch.Query
import scala.io.Source

/**
  * Reads queries for the tipster dataset
  */
object QueryReader {

    /**
      * Reads the queries from given filename
      * 
      * @param filename the file to read from
      * @return the list of queries
      */
    def read(filename:String) : List[Query] = {
        val nums = Source.fromFile(filename).getLines.
            filter(_.startsWith("<num>")).
            map(line => line.replaceAll("[^0-9]*", "").toInt)

        val topics = Source.fromFile(filename).getLines.
            filter(_.startsWith("<title>")).
            map(line => line.replaceAll("<title>\\s*Topic:\\s*(.*)\\s*$", "$1"))

        return nums.zip(topics).map{
            case(num:Int, topic:String) => new Query(num, topic)
        }.toList
    }
    
}

