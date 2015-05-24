import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object DiamondSquare {
  def generateHeightMap(size: Int, seed: Long = 0): ArrayBuffer[ArrayBuffer[Short]] = {
    val diamondSquare: DiamondSquare = new DiamondSquare(size, seed)
    diamondSquare.diamondSquare()
    diamondSquare.map
  }
}



class DiamondSquare(size: Int, seed: Long) {
  val random = new Random(seed)
  val map = ArrayBuffer.fill[Short](size, size)(32000)
  val sizeMinusOne: Int = size - 1

  def square(x: Int, y: Int, sideLength: Int, halfSide: Int, altitude: Int) = {
    val average = (
      map(x)(y)
        + map(x + sideLength)(y)
        + map(x)(y + sideLength)
        + map(x + sideLength)(y + sideLength)
      ) / 4
    map(x + halfSide)(y + halfSide) = (average + (random.nextDouble() * 2 * altitude) - altitude).toShort
  }

  def diamond(x: Int, y: Int, halfSide: Int, altitude: Int) = {
    val average = (
      map((x - halfSide + sizeMinusOne) % sizeMinusOne)(y)
        + map((x + halfSide) % sizeMinusOne)(y)
        + map(x)((y + halfSide) % sizeMinusOne)
        + map(x)((y - halfSide + sizeMinusOne) % sizeMinusOne)
      ) / 4
    val avg = (average + (random.nextDouble() * 2 * altitude) - altitude).toShort
    map(x)(y) = avg
    if (x == 0) map(sizeMinusOne)(y) = avg
    if (y == 0) map(x)(sizeMinusOne) = avg
  }

  def diamondSquare() = {
    var altitude = 32000
    for (sideLength <- Stream.iterate(sizeMinusOne)(_ / 2).takeWhile(_ >= 2)) {
      val halfSide = sideLength / 2
      for (x <- 0 until sizeMinusOne by sideLength) {
        for (y <- 0 until sizeMinusOne by sideLength) {
          square(x, y, sideLength, halfSide, altitude)
        }
      }
      for (x <- 0 until sizeMinusOne by halfSide) {
        for (y <- (x + halfSide) % sideLength until sizeMinusOne by sideLength) {
          diamond(x, y, halfSide, altitude)
        }
      }
      altitude = (altitude / 1.7).toInt
    }
  }
}
