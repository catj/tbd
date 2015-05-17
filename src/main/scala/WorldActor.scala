import akka.actor.{Props, Actor}

import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import TileType._

object WorldActor {
  def props: Props = Props(new WorldActor)
}

class WorldActor extends Actor {
  override def receive: Receive = {
    case "start" =>
        val heightMap = DiamondSquare.generateHeightMap(129, System.currentTimeMillis())
      sender ! SaveWorld(heightMap)
  }
}
