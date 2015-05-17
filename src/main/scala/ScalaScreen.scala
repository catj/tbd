import com.googlecode.lanterna.TextColor.RGB
import com.googlecode.lanterna._
import com.googlecode.lanterna.screen.Screen


object ScalaScreen {
  def setString(screen: Screen, position: TerminalPosition, terminalSize: TerminalSize, outputString: String, offset: Int = 0): Unit = {
    for ((x, i) <- outputString.view.zipWithIndex) {
      screen.setCharacter(position.withRelativeColumn(offset + i), new TextCharacter(x))
    }
  }

  def setMapString(screen: Screen, position: TerminalPosition, size: TerminalSize, heights: Array[Int]): Unit = {
    val colorInterpolation = new ColorInterpolation(heights.min, heights.max, ColorSchemes.FiveColorBlindFriendly)
    for ((height, i) <- heights.zipWithIndex) {
      val color = colorInterpolation.interpolate(height)
      screen.setCharacter(position.withRelativeColumn(i), new TextCharacter('#', new RGB(color.red, color.green, color.blue) , TextColor.ANSI.BLACK, SGR.BOLD))
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
