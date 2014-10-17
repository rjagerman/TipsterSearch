package ch.ethz.inf.da.tipstersearch.parsing

class TextParser {
    
    def parse(text:String): List[String] = {
        tokenize(text.replaceAll("[ \\_;:(),]+", " ")).map(s => s.replaceAll("[`'\"\\.]", ""))
    }

    def tokenize(text:String) : List[String] = {
        text.split(" ").filter(s => s != "").toList
    }

}