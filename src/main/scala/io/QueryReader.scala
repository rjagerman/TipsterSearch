package ch.ethz.dal.tipstersearch.io

import scala.io.Source

class QueryReader {
    def read(filename:String) : List[(Int,String)] = {
        val nums = Source.fromFile(filename).getLines.
            filter(_.startsWith("<num>")).
            map(line => line.replaceAll("[^0-9]*", "").toInt)

        val topics = Source.fromFile(filename).getLines.
            filter(_.startsWith("<title>")).
            map(line => line.replaceAll("<title>\\s*Topic:\\s*(.*)\\s*$", "$1"))

        return nums.zip(topics).toList
    }
}

