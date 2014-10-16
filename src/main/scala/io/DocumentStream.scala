package ch.ethz.dal.tipstersearch.io

import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.io.InputStream
import scala.collection.JavaConversions

class DocumentStream {

    def read(path:String) : Iterator[ZipEntry] = {
    	val zf = new ZipFile(path)
    	val it = JavaConversions.enumerationAsScalaIterator(zf.entries())
    	it
    }
}





