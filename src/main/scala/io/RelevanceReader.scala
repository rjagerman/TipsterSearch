package ch.ethz.inf.da.tipstersearch.io

import ch.ethz.inf.da.tipstersearch.Query
import scala.io.Source

object RelevanceReader{
	def read(path:String, queries:List[Query]) {
		for(query <- queries) {
			query.truth = Source.fromFile(path).getLines
				.filter(line => line.startsWith("" + query.id))
            	.map(line => (line.replaceAll("^[0-9]+ 0 ([^ ]+).*", "$1"), line.replaceAll(".*([01])$", "$1").toInt) )
            	.toList
		}
	}
}

