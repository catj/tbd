import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset

import com.googlecode.lanterna.terminal.ansi.{CygwinTerminal, UnixTerminal}
import com.googlecode.lanterna.terminal.{Terminal, TerminalFactory}
import ScalaTerminalFactory._

object ScalaTerminalFactory {
  val DefaultOutputStream: OutputStream = System.out
  val DefaultInputStream: InputStream = System.in
  val DefaultCharset: Charset = Charset.forName(System.getProperty("file.encoding"))
}

class ScalaTerminalFactory extends TerminalFactory {
  override def createTerminal(): Terminal = {
    if (isOperatingSystemWindows) {
      createCygwinTerminal(DefaultOutputStream, DefaultInputStream, DefaultCharset)
    }
    else {
      createUnixTerminal(DefaultOutputStream, DefaultInputStream, DefaultCharset)
    }
  }

  private def createCygwinTerminal(outputStream: OutputStream, inputStream: InputStream, charset: Charset): Terminal = {
    new CygwinTerminal(inputStream, outputStream, charset)
  }

  private def createUnixTerminal(outputStream: OutputStream, inputStream: InputStream, charset: Charset): Terminal = {
    new UnixTerminal(inputStream, outputStream, charset)
  }

  private def isOperatingSystemWindows: Boolean = {
    System.getProperty("os.name", "").toLowerCase.startsWith("windows")
  }
}
