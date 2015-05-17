import ScalaScreen._

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
      val colorInterpolation = new ColorInterpolation(heightMap.flatten.min, heightMap.flatten.max, ColorSchemes.SevenColorBlindFriendly)
      heightMap.toArray.zipWithIndex.foreach { case (row, i) =>
        setMapString(screen, new TerminalPosition(0, i), terminalSize, row.toArray, colorInterpolation)
      }
      screen.refresh()
    case "redraw" =>
      self ! WorldState(state.get)
  }
}
