import akka.actor.{Actor, Props}

import scala.collection.mutable.ArrayBuffer

object WorldActor {
  def props(size: Int): Props = Props(new WorldActor(size))
}

class WorldActor(size: Int) extends Actor {

  def generateTemperatureMap(heightMap: ArrayBuffer[ArrayBuffer[Short]]): ArrayBuffer[ArrayBuffer[Short]] = {
    val map = ArrayBuffer.fill[Short](heightMap.size, heightMap.size)(0)
    map
  }

  override def receive: Receive = {
    case "start" =>
      val seed = System.currentTimeMillis()
      val heightMap = DiamondSquare.generateHeightMap(size, seed)

      val temperatureMap = generateTemperatureMap(heightMap)
      sender ! SaveWorld(heightMap, seed)
  }
}
