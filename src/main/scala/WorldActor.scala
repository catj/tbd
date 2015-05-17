

object WorldActor {
  def props: Props = Props(new WorldActor)
}

class WorldActor extends Actor {
  override def receive: Receive = {
    case "start" =>
      val heightMap = DiamondSquare.generateHeightMap(1025, 5)
      sender ! SaveWorld(heightMap)
  }
}
