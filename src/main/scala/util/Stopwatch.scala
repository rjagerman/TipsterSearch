package ch.ethz.inf.da.tipstersearch.util

/**
  * Basic stopwatch, useful for timing long running functions
  */
class Stopwatch {

    private var startTime:Long = 0

    /**
      * Starts the timer or restarts it if it was already running
      */
    def start = { startTime = System.nanoTime }

    /**
      * @return the number of hours since start was called
      */
    def hours = minutes/60

    /**
      * @return the number of minutes since start was called
      */
    def minutes = seconds/60

    /**
      * @return the number of seconds since start was called
      */
    def seconds = milliseconds/1000

    /**
      * @return the number of milliseconds since start was called
      */
    def milliseconds = (System.nanoTime-startTime)/1000000

    start

    /**
      * @return string representation of the time passed so far
      */
    override def toString : String = {
        var output = ""
        if(hours >= 1) {
            output += hours + " hour"
            if(hours>1) output += "s"
        }
        if(minutes%60 >= 1) {
            if(hours >= 1) { output += " " }
            output += minutes%60 + " minute"
            if(minutes%60>1) output += "s"
        }
        if(seconds%60 >= 1) {
            if(minutes >= 1) { output += " " }
            output += seconds%60 + " second"
            if(seconds%60>1) output += "s"
        } else if(seconds < 1) {
            output += milliseconds + "ms"
        }
        output
    }

}

