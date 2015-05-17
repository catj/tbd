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
  private var state: Option[ArrayBuffer[ArrayBuffer[Int]]] = None
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
      heightMap.toArray.zipWithIndex.foreach { case (row, i) =>
        setMapString(screen, new TerminalPosition(0, i), terminalSize, row.toArray)
      }
      screen.refresh()
    case "redraw" =>
      self ! WorldState(state.get)
  }
}
