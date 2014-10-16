package ch.ethz.inf.da.tipstersearch

import scala.io.Source
import ch.ethz.inf.da.tipstersearch.io.QueryReader

object Main {
	def main(args:Array[String]) {

        val qr = new QueryReader()
        val queries = qr.read("tipster/topics")

        queries.foreach{ println }
	}
}

