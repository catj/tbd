import akka.actor.{Actor, Props}

import scala.collection.mutable.ArrayBuffer

object WorldActor {
  def props: Props = Props(new WorldActor)
}

class WorldActor extends Actor {

  def generateTemperatureMap(heightMap: ArrayBuffer[ArrayBuffer[Short]]): ArrayBuffer[ArrayBuffer[Short]] = {
    val map = ArrayBuffer.fill[Short](heightMap.size, heightMap.size)(0)
    map
  }

  override def receive: Receive = {
    case "start" =>
      val seed = System.currentTimeMillis()
      val heightMap = DiamondSquare.generateHeightMap(1025, seed)

      val temperatureMap = generateTemperatureMap(heightMap)
      sender ! SaveWorld(heightMap, seed)
  }
}
