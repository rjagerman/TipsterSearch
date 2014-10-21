package ch.ethz.inf.da.tipstersearch.processing

/**
  * Processes numbers and finance in text
  * 
  * 1. Anything containing financial symbols gets split up and financial symbols converted to text
  * 2. Other numbers add their respective order-of-magnitude features (k or m)
  */
object NumberProcessing {
    val finance = """^[\$¥€£0-9]+[km]?$""".r
    val digit = """^\d+$""".r

    def process(token:String):List[String] = {
        if(digit.pattern.matcher(token).matches) {
            if(token.length > 6) { return List(token, token.take(token.length-6) + "m") } // order-of-magnitude in millions
            if(token.length > 3) { return List(token, token.take(token.length-3) + "k") } // order-of-magnitude in thousands
        } else if(finance.pattern.matcher(token).matches) {
            return token.split("((?<=[\\$¥€£])|(?=[\\$¥€£]))").filter(x => x.length>0).map{
                    case "$" => "dollar"
                    case "¥" => "yen"
                    case "€" => "euro"
                    case "£" => "pound"
                    case x => x
                }.toList // Split financial symbols from numbers
        }
        return List(token) // do nothing
    }

    
}
