import akka.actor.{Actor, Props}

import scala.collection.mutable.ArrayBuffer
import scala.math._

object WorldActor {
  def props(size: Int): Props = Props(new WorldActor(size))
}

class WorldActor(size: Int) extends Actor {

  def generateTemperatureMap(heightMap: ArrayBuffer[ArrayBuffer[Short]]): ArrayBuffer[ArrayBuffer[Short]] = {
    val map = ArrayBuffer.fill[Short](heightMap.size, heightMap.size)(0)
    val equator = heightMap.size / 2
    val latitudeNorm: Double = 180.0 / heightMap.size
    map.toArray.zipWithIndex.foreach {
      case (row, i) =>
        val distanceFromEquator = abs(equator - i)
        val heightRow = heightMap.apply(i)
        row.toArray.zipWithIndex.foreach {
          case (column, j) =>
            val flatTemp = ((5 * random) + (40 - (distanceFromEquator * latitudeNorm))).toShort
            heightRow.apply(j) match {
              case height: Short if height > 0 =>
                row.update(j, (flatTemp - (height / 1000 * 2)).toShort)
              case _ =>
                row.update(j, flatTemp)
            }
        }
    }
    map
  }

  override def receive: Receive = {
    case "start" =>
      val seed = System.currentTimeMillis()
      val heightMap = DiamondSquare.generateHeightMap(size, seed)

      val temperatureMap = generateTemperatureMap(heightMap)
      sender ! SaveWorld(heightMap, temperatureMap, seed)
  }
}
