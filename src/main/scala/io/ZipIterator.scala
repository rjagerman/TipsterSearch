package ch.ethz.inf.da.tipstersearch.io

import java.util.zip.{ZipEntry, ZipInputStream}
import java.io.{ByteArrayInputStream, InputStream}

/** This is an iterator that handles zip files and recursively traverses embedded zip files.
  * It returns (filename:String, filecontents:InputStream) for every non zip file it finds
  * inside the zip provided by the InputStream
  * 
  * @constructor create a new zip iterator with given input stream
  * @param is the input stream
  */
class ZipIterator(is:InputStream) extends Iterator[(String, InputStream)] {

    private var zis:ZipInputStream = new ZipInputStream(is)
    private var entry:ZipEntry = zis.getNextEntry
    private var child:ZipIterator = null

    /** Checks if there is still an element to obtain
      */
    def hasNext : Boolean = {
        return entry != null
    }

    /** Gets the next (filename:String, filecontents:InputStream) element from the stream
      */
    def next : (String, InputStream) = {

        // Check if a child zip is opened and use that instead
        if(child != null) {
            if(child.hasNext)
                return child.next
            else
                child.zis.close()
                child = null
        }

        // Generate a stream based on file contents
        val output:InputStream = generateEntryStream(zis, entry.getSize().toInt)
        zis.closeEntry()

        // If the file is a zip, create a new child ZipStream and use that
        if(entry.getName().endsWith(".zip")) {
            child = new ZipIterator(output)
            if(child.hasNext) {
                entry = zis.getNextEntry
                return child.next
            }
        }

        // Return the actual entry
        val name = entry.getName()
        entry = zis.getNextEntry
        return (name, output)

    }

    /** Generates a byte stream for the current entry in the given ZipInputStream
      * This is necessary because closing ZipInputStreams is not wanted yet most
      * parsers are not aware of this restriction and prematurely close the stream
      * 
      * @param is The zip input stream to obtain the entry from
      * @param size The size of the entry
      * @return A byte-wise input stream of the uncompressed current entry
      */ 
    private def generateEntryStream(is:ZipInputStream, size:Int) : InputStream = {
        var buffer:Array[Byte] = new Array[Byte](size)
        var read:Int = 0
        var c:Int = 0
        while(read != -1 && c != size) {
            read = is.read(buffer, c, size-c)
            c += read
        }
        new ByteArrayInputStream(buffer)
    }

}

