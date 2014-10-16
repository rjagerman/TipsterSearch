package ch.ethz.inf.da.tipstersearch

import scala.io.Source
import ch.ethz.inf.da.tipstersearch.io.QueryReader
import ch.ethz.inf.da.tipstersearch.io.DocumentStream

object Main {
    def main(args:Array[String]) {

        val qr = new QueryReader()
        val queries = qr.read("dataset/topics")

        val ds = new DocumentStream()
        var count = 0
        for(doc:String <- ds.readDirectory("dataset/tipster")) {
            count += 1
            if(count % 1000 == 0) {
                println(count)
            }
        }

    }
}

