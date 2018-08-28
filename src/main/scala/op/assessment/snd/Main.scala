package op.assessment.snd

import op.assessment.snd.CharDraw.Board
import scala.annotation.tailrec

object Main extends App {

  import scala.io.StdIn._
  import Out._

  @tailrec
  def program(
      continued: Boolean, board: Board)(
      next: (String, Board) => Board): Unit = if (continued) {

    println("enter command:")
    readLine() match {
      case "Q" => program(continued = false, board)(next)
      case cmd => program(continued = true, next(cmd, board))(next)
    }
  } else {
    println("Good bye!")
  }

  program(continued = true, Board.empty) { (cmd, board) =>
    board.validate(cmd) match {
      case Some(e) =>
        val b = board.updated(e)
        b.out()
        b

      case None =>
        println("Cannot execute command: either wrong command format or bord unchanged")
        board
    }
  }
}
