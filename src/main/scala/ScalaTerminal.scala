import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import com.googlecode.lanterna.terminal.{DefaultTerminalFactory, Terminal}

object ScalaTerminal {
  def terminal: Terminal = new DefaultTerminalFactory()
    .setSwingTerminalFrameAutoCloseTrigger(SwingTerminalFrame.AutoCloseTrigger.CloseOnExitPrivateMode)
    .createTerminal()
}