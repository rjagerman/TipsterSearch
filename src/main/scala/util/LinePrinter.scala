package ch.ethz.inf.da.tipstersearch.util

/**
  * For printing output on the same line (i.e. progress displays)
  */
object LinePrinter {

    /**
      * Removes the current line in the console and prints given obj to it.
      * 
      * @param obj the object to print
      */
    def print(obj: Any) {
        Console.print("\033[2K\r")
        Console.print(obj)
    }

    /**
      * Removes the current line in the console and prints given obj to it
      * ending with a new line.
      * 
      * @param obj the object to print
      */
    def println(obj: Any) {
        Console.print("\033[2K\r")
        Console.println(obj)
    }
    
}
