package ch.ethz.inf.da.tipstersearch.processing

import com.github.aztek.porterstemmer.PorterStemmer

/**
  * Processes text strings and returns lists of cleaned up and processed tokens
  */
object TextProcessor {

    /**
      * Processes given string by tokenizing, removing stopwords and stemming
      * 
      * @param text the text to process
      * @return the processed tokens as a list of strings
      */
    def process(text:String) : List[String] = {
        Tokenizer.tokenize(text.toLowerCase).par // Tokenize the lower case form of the text
            .map(s => s.replaceAll("[^a-zA-Z0-9\\$¥€£]+", "")) // Filter out non informative text stuff (apostrophes and such)
            .flatMap(s => NumberProcessing.process(s)) // Process digits in text
            .filter(x => !Stopwords.set(x)) // Filter stop words
            .map(s => PorterStemmer.stem(s)).toList // Stem words
    }
    
}