import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

import akka.actor._

import scala.collection.mutable.ArrayBuffer

sealed trait State

case object Menu extends State

case object Generation extends State

case object Render extends State

case object Play extends State

case object Exit extends State

sealed trait Data

case object Uninitialized extends Data

case class WorldState(heightMap: ArrayBuffer[ArrayBuffer[Short]], temperatureMap: ArrayBuffer[ArrayBuffer[Short]]) extends Data

case class StartMenu(ref: ActorRef)

case class GenerateWorld(ref: ActorRef)

case class RenderWorld(ref: ActorRef)

case class SaveWorld(heightMap: ArrayBuffer[ArrayBuffer[Short]], temperatureMap: ArrayBuffer[ArrayBuffer[Short]], seed: Long)

case class Exit(ref: ActorRef)

object Game extends App {
  val system = ActorSystem("gameSystem")
  val gameActor = system.actorOf(Props[Game], "gameMain")
  gameActor ! StartMenu(gameActor)
  gameActor ! GenerateWorld(gameActor)
  sys addShutdownHook {
    gameActor ! Kill
    system.shutdown()
  }
}

class Game extends LoggingFSM[State, Data] {

  val WorldSize = 33
  val renderActorRef = context.actorOf(RenderActor.props, name = "renderActor")
  val worldActorRef = context.actorOf(WorldActor.props(WorldSize), name = "worldActor")

  startWith(Menu, Uninitialized)

  when(Menu) {
    case Event(StartMenu(ref), Uninitialized) => goto(Generation)
  }

  def saveWorld(heights: ArrayBuffer[ArrayBuffer[Short]], temperatureMap: ArrayBuffer[ArrayBuffer[Short]], seed: Long) = {
  //  val image = new BufferedImage(65, 65, BufferedImage.TYPE_INT_RGB)
  //  val colorInterpolation = new ColorInterpolation(heights.flatten.min, heights.flatten.max, ColorSchemes.NineClassSpectral)

  //  heights.toArray.zipWithIndex.foreach { case (row, i) =>
   //   row.toArray.zipWithIndex.foreach { case (cell, j) =>
   //     val rgbColor = colorInterpolation.interpolate(cell)
    //    val awtColor = new Color(rgbColor.red, rgbColor.green, rgbColor.blue)
     //   image.setRGB(i, j, awtColor.getRGB)
   //   }
   // }
   // image.flush()
  //  ImageIO.write(image, "BMP", new File("file" + seed + ".bmp"))

  }

  when(Generation) {
    case Event(GenerateWorld(ref), Uninitialized) =>
      worldActorRef ! "start"
      stay()

    case Event(SaveWorld(heightMap, temperatureMap, seed), Uninitialized) =>
      saveWorld(heightMap, temperatureMap, seed)
      goto(Render) using WorldState(heightMap = heightMap, temperatureMap = temperatureMap)
  }

  when(Render) {
    case Event(RenderWorld(ref), WorldState(heightMap, temperatureMap)) =>
      renderActorRef ! stateData
      stay()
    case Event("redraw", WorldState(heightMap, temperatureMap)) =>
      renderActorRef ! stateData
      stay()
  }

  when(Exit) {
    case Event(Exit(ref), Uninitialized) =>
      stop(FSM.Normal)
  }

  onTransition {
    case Generation -> Render => self ! RenderWorld(self)
  }

  onTermination {
    case StopEvent(FSM.Normal, state, data) =>
      sys.exit()
  }

  initialize()
}
