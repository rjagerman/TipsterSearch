package ch.ethz.inf.da.tipstersearch.parsing

object Tokenizer {

    def tokenize(text:String) : List[String] = {
        text.replaceAll("[ \\_;:(),]+", " ")
        	.split(" ")
        	.map(s => s.replaceAll("[`'\"\\.]*", ""))
        	.filter(s => s != "")
        	.toList
    }

}