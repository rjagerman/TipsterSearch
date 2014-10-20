package ch.ethz.inf.da.tipstersearch

import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.{Document => XMLDocument, NodeList}
import ch.ethz.inf.da.tipstersearch.processing.TextProcessor

/** Represents a single document in the tipster collection
  *
  * @param is the InputStream to read from
  */
class Document(is:InputStream) {

    val xml:XMLDocument = Document.documentBuilder.parse(is)
    val name:String = read(xml.getElementsByTagName("DOCNO")).replaceAll("[ \\t\\n]+", "")
    val title:String = read(xml.getElementsByTagName("HEAD"))
    val text:String = read(xml.getElementsByTagName("TEXT")).replaceAll("[ \\t\\n]+", " ")
    val titleTokens:List[String] = TextProcessor.process(title)
    val textTokens:List[String] = TextProcessor.process(text)
    val tokens:List[String] = titleTokens ++ textTokens

    protected def read(nlist: NodeList) : String = {
        if (nlist==null) ""
        else if (nlist.getLength==0) ""
        else {
            val length = nlist.getLength
            val text  = for (i <- 0 until length) yield nlist.item(i).getTextContent
            text.mkString(System.getProperty("line.separator"))
        }
     }

}

object Document {
    val documentBuilder  = DocumentBuilderFactory.newInstance().newDocumentBuilder()
}
