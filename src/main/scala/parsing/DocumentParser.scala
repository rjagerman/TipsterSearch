package ch.ethz.inf.da.tipstersearch.parsing

import scala.xml.XML
import scala.xml.Elem

class DocumentParser {
    
    def parse(doc:String): (String, String) = {
        try {
            val e:Elem = XML.loadString(doc)
            (extractIdFromXml(e), extractTextFromXml(e))
        } catch {
            case e: Exception => return (extractIdFromXmlString(doc), extractTextFromXmlString(doc))
        }
    }

    def extractIdFromXml(e:Elem) : String = {
        (e \\ "DOCNO").text.replaceAll("[ \\t\\n\\-]*", "")
    }

    def extractIdFromXmlString(doc:String) : String = {
        doc.replaceFirst("(?s).*<DOCNO>(.*)</DOCNO>.*", "$1").replaceAll("[ \\t\\n\\-]*", "")
    }

    def extractTextFromXml(e:Elem) : String = {
        ((e \ "HEAD").text + " " + (e \ "TEXT").text).replaceAll("[ \\t\\n]+", " ")
    }

    def extractTextFromXmlString(doc:String) : String = {
        doc.replaceAll(".*<HEAD>(.*)</HEAD>.*<TEXT>(.*)</TEXT>.*", "$1 $2")
    }

}