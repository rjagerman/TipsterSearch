package ch.ethz.inf.da.tipstersearch.processing

import com.github.aztek.porterstemmer.PorterStemmer

/** Processes text strings to tokenize and clean them up
  */
object TextProcessor {

	/** Processes given string by tokenizing, removing stopwords and stemming
	  * 
	  * @param text the text to process
	  * @return the processed tokens as a list of strings
	  */
    def process(text:String) : List[String] = {
        Tokenizer.tokenize(text.toLowerCase).par // Tokenize lower case text
        	.filter(s => !Stopwords.set(s)) // Filter stop words
        	.map(s => PorterStemmer.stem(s)).toList // Stem words
    }
}