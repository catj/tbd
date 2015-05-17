import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}

object ScalaTerminal {
  def terminal: Terminal = new DefaultTerminalFactory().createTerminal()
}