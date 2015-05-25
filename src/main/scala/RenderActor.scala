import ScalaScreen._
import akka.actor.{Actor, Props}
import com.googlecode.lanterna.screen.{Screen, TerminalScreen}
import com.googlecode.lanterna.terminal.{ResizeListener, Terminal}
import com.googlecode.lanterna.{TerminalPosition, TerminalSize}

import scala.collection.mutable.ArrayBuffer

object RenderActor {
  def props: Props = Props(new RenderActor)
}

class RenderActor extends Actor {
  private var heightState: Option[ArrayBuffer[ArrayBuffer[Short]]] = None
  private var temperatureState: Option[ArrayBuffer[ArrayBuffer[Short]]] = None
  val terminal = ScalaTerminal.terminal
  val screen: Screen = new TerminalScreen(terminal)
  val listener: ResizeListener = new ResizeListener {
    override def onResized(terminal: Terminal, newSize: TerminalSize): Unit = {
      self ! "redraw"
    }
  }
  terminal.addResizeListener(listener: ResizeListener)
  screen.startScreen()

  override def receive: Receive = {
    case WorldState(heightMap, temperatureMap) =>
      val terminalSize = resize(screen)
      heightState = Option(heightMap)
      temperatureState = Option(temperatureMap)
      val colorInterpolation = new ColorInterpolation(heightMap.flatten.min, heightMap.flatten.max,
        ColorSchemes.NineClassSpectralRev)

      heightMap.toArray.zipWithIndex.foreach { case (row, i) =>
        setMapString(screen, new TerminalPosition(0, i), terminalSize, row.toArray, colorInterpolation)
        setLandMapString(screen, new TerminalPosition(heightMap.size + 1, i), terminalSize, row.toArray)
      }

      val tempColorInterpolation = new ColorInterpolation(-60, 60, ColorSchemes.NineClassSpectralRev)
      temperatureMap.toArray.zipWithIndex.foreach { case (row, i) =>
        setMapString(screen, new TerminalPosition(heightMap.size * 2 + 2, i), terminalSize, row.toArray, tempColorInterpolation)
      }

      screen.refresh()
    case "redraw" =>
      self ! WorldState(heightState.get, temperatureState.get)
  }

  override def postStop() = {
    screen.stopScreen()
    terminal.exitPrivateMode()
  }
}
