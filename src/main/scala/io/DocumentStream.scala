package ch.ethz.inf.da.tipstersearch.io

import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.FileInputStream
import java.io.File
import scala.collection.JavaConversions
import scala.io.Source

class ZipIterator(zis: ZipInputStream) extends Iterator[ZipEntry] {
    private var entry:ZipEntry = zis.getNextEntry
    def hasNext = {
        entry != null
    }
    def next = {
        val output:ZipEntry = entry
        entry = zis.getNextEntry
        output
    }
}

class DocumentStream {

    def readDirectory(path:String) : Iterator[String] = {
        new File(path).listFiles.iterator
            .filter(f => f.getName.endsWith(".zip"))
            .flatMap(f => read(f.getAbsolutePath))
    }

    def read(path:String) : Iterator[String] = {
        expand(new ZipInputStream(new BufferedInputStream(new FileInputStream(path))))
    }

    def expand(zis:ZipInputStream) : Iterator[String] = {
        new ZipIterator(zis)
            .map(x => (x, zis))
            .flatMap{
                case (zem,zism) =>
                    if(zem.getName().endsWith(".zip"))
                        expand(new ZipInputStream(zism))
                    else
                        List(Source.fromInputStream(zism).mkString)
            }
    }

}



