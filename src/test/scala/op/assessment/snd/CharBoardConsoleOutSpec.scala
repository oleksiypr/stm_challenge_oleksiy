package op.assessment.snd

import org.scalatest.FunSuite

class CharBoardConsoleOutSpec extends FunSuite {

  import CharDraw.Board
  import Out._

  test("Zero sized empty board out") {
    var printedOut = ""

    val boardOut = new CharBoardConsoleOut {
      override def out(s: String): Unit = {
        printedOut += s
      }
    }

    boardOut.out(Board.empty)

    assert(printedOut ===
      """--
        /--
        /""".stripMargin('/'))
  }

  test("1 by 1 sized empty board out") {
    var printedOut = ""

    val boardOut = new CharBoardConsoleOut {
      override def out(s: String): Unit = {
        printedOut += s
      }
    }

    boardOut.out(Board(Vector(Vector(' '))))

    assert(printedOut ===
      """---
        /| |
        /---
        /""".stripMargin('/'))
  }

  test("3 by 4 sized empty board out") {
    var printedOut = ""

    val boardOut = new CharBoardConsoleOut {
      override def out(s: String): Unit = {
        printedOut += s
      }
    }

    boardOut.out(Board(Vector(
      Vector(' ', ' ', ' ', 'c'),
      Vector(' ', 'x', 'x', ' '),
      Vector(' ', ' ', 'x', ' ')
    )))

    assert(printedOut ==
      """------
        /|   c|
        /| xx |
        /|  x |
        /------
        /""".stripMargin('/')
    )
  }
}
