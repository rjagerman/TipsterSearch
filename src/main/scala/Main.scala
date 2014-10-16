package ch.ethz.inf.da.tipstersearch

import scala.io.Source
import ch.ethz.dal.tipstersearch.io.QueryReader
import ch.ethz.dal.tipstersearch.io.DocumentStream

object Main {
	def main(args:Array[String]) {

        val qr = new QueryReader()
        val queries = qr.read("tipster/topics")

        val ds = new DocumentStream()
        ds.read("tipster/zips-1.zip").foreach{ e => println(e.getName()) }

        //queries.foreach{ println }
	}
}

