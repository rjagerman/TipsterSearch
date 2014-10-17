package ch.ethz.inf.da.tipstersearch.parsing

import scala.xml.XML
import scala.xml.Elem

class DocumentParser {
    
    def parse(doc:String): (String, String) = {
        val e:Elem = XML.loadString(doc)
        (extractIdFromXml(e), extractTextFromXml(e))
    }

    def extractIdFromXml(e:Elem) : String = {
        (e \\ "DOCNO").text.replaceAll("[ \\t\\n\\-]*", "")
    }

    def extractTextFromXml(e:Elem) : String = {
        ((e \ "HEAD").text + " " + (e \ "TEXT").text).replaceAll("[ \\t\\n]+", " ")
    }

}