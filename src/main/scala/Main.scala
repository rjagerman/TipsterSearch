import scala.io.Source

object Main {
	def main(args:Array[String]) {
		Source.fromFile("tipster/topics").foreach{print}
	}
}

