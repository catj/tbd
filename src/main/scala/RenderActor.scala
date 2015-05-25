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
  private var state: Option[ArrayBuffer[ArrayBuffer[Short]]] = None
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
    case WorldState(heightMap) =>
      val terminalSize = resize(screen)
      state = Option(heightMap)
      val colorInterpolation = new ColorInterpolation(heightMap.flatten.min, heightMap.flatten.max, ColorSchemes.NineClassSpectral)
      heightMap.toArray.zipWithIndex.foreach { case (row, i) =>
        i match {
          case x: Int if x <= (heightMap.size / 2) =>
            setMapString(screen, new TerminalPosition(0, i), terminalSize, row.toArray, colorInterpolation)
          case x: Int if x > (heightMap.size / 2) =>
            setMapString(screen, new TerminalPosition(row.length + 2, i - (row.length / 2) - 1 ), terminalSize, row.toArray, colorInterpolation)
        }
      }
      screen.refresh()
    case "redraw" =>
      self ! WorldState(state.get)
  }

  override def postStop() = {
    screen.stopScreen()
    terminal.exitPrivateMode()
  }
}
