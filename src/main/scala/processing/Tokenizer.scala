package ch.ethz.inf.da.tipstersearch.processing

/**
  * Tokenizes strings and removes punctuation
  */
object Tokenizer {

    /**
      * Tokenize given string, this also converts the tokens to lowercase
      * 
      * @param text the text to tokenize
      * @return the tokens as a list of strings
      */
    def tokenize(text:String) : List[String] = {
        text.split("[ \\_;:\\(\\),\\.\\-\\?\\!]+").toList
    }

}