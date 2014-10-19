package ch.ethz.inf.da.tipstersearch.util

object Stopwatch {
	private var startTime:Long = 0
	private var endTime:Long = 0
	def start = { startTime = System.nanoTime }
	def stop = { endTime = System.nanoTime }
	def hours = minutes/60
	def minutes = seconds/60
	def seconds = milliseconds/1000
	def milliseconds = (endTime-startTime)/1000000
	def printTime = {
		stop
		if(hours >= 1) {
			print(hours + " hour")
			if(hours>1) print("s ") else print(" ")
		}
		if(minutes >= 1) {
			print(minutes%60 + " minute")
			if(minutes%60>1) print("s ") else print(" ")
		}
		if(seconds >= 1) {
			print(seconds%60 + " second")
			if(seconds%60>1) print("s ")
		} else {
			print(milliseconds + "ms")
		}
		println()
	}
}