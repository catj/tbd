import com.googlecode.lanterna.TextColor.RGB
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.{TerminalPosition, TerminalSize, TextCharacter, TextColor}


object ScalaScreen {
  def setString(screen: Screen, position: TerminalPosition, terminalSize: TerminalSize, outputString: String, offset: Int = 0): Unit = {
    for ((x, i) <- outputString.view.zipWithIndex) {
      screen.setCharacter(position.withRelativeColumn(offset + i), new TextCharacter(x))
    }
  }

  def setMapString(screen: Screen, position: TerminalPosition, size: TerminalSize, heights: Array[Short], colorInterpolation: ColorInterpolation): Unit = {
    for ((height, i) <- heights.zipWithIndex) {
      val color = colorInterpolation.interpolate(height)
      screen.setCharacter(position.withRelativeColumn(i), new TextCharacter('\u00A7', new RGB(color.red, color.green, color.blue), TextColor.ANSI.BLACK))
    }
  }

  def setLandMapString(screen: Screen, position: TerminalPosition, size: TerminalSize, heights: Array[Short]): Unit = {
    for ((height, i) <- heights.zipWithIndex) {
      height match {
        case h: Short if h > 0 =>
          screen.setCharacter(position.withRelativeColumn(i), new TextCharacter('\u00A7', new RGB(55, 255, 55), TextColor.ANSI.BLACK))
        case _ =>
          screen.setCharacter(position.withRelativeColumn(i), new TextCharacter('\u00A7', new RGB(55, 55, 255), TextColor.ANSI.BLACK))
      }
    }
  }

  def resize(screen: Screen): TerminalSize = {
    val resize = screen.doResizeIfNecessary()
    if (resize != null) {
      screen.clear()
      resize
    } else {
      screen.getTerminalSize
    }
  }
}
