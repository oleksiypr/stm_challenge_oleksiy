package op.assessment.snd

import op.assessment.snd.CharDraw.Board

trait Out[T] {
  def out(t: T): Unit
}

trait ConsoleOut[T] extends Out[T] {
  def out(s: String): Unit = print(s)
}

object Out {

  def apply[T: Out](t: T): Unit = {
    implicitly[Out[T]].out(t)
  }

  implicit class OutOps[T](val t: T) extends AnyVal {
    def out()(implicit ev: Out[T]): Unit = Out[T](t)
  }

  trait CharBoardConsoleOut extends ConsoleOut[Board] {

    override def out(board: Board): Unit = {
      val (h, w) = board.size
      val horizontalLine = Array.fill(w + 2)("-").mkString("")
      outLn(horizontalLine)
      for {
        y <-  0 until h
        x <- -1 to w
      } {
        if (x == -1) out("|") else
        if (x == w) outLn("|")
        else out(board(x, y).toString)
      }
      outLn(horizontalLine)
    }

    private def outLn(s: String): Unit = {
      out(s)
      out("\n")
    }
  }

  implicit object charBoardConsoleOut extends CharBoardConsoleOut
}
