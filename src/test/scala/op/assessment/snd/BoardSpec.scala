package op.assessment.snd

import op.assessment.snd.BoardSpec.TestFakes
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

object BoardSpec {

  trait TestFakes extends Draw {
    override type Pixel = Char
    override val whitePixel: Pixel = ' '
    override def draw(line: Line)(implicit board: Board): Board = Board.empty
    override def validate(cmd: String)(implicit board: Board): Option[Event]= None
    def fill(x: Int, y: Int, c: Pixel)(implicit board: Board): Board = Board.empty
  }
}

class BoardSpec extends WordSpecLike with Matchers with BeforeAndAfterAll {

  "Board" should {
    "be created" in new Draw with TestFakes {

      val board: Board = Board.empty.updated(Created(heights = 2, width = 3))
      board shouldBe {
        Board(Vector(
          Vector(' ', ' ', ' '),
          Vector(' ', ' ', ' ')
        ))
      }

      board.size shouldBe (2, 3)
    }

    "be updated with lines" in new Draw with TestFakes {

      var linesDrown = Set.empty[Line]

      override def draw(line: Line)(implicit board: Board): Board = {
        linesDrown += line
        Board(Vector(
          Vector('x', 'x', 'x', ' '),
          Vector('x', ' ', 'x', ' '),
          Vector('x', 'x', 'x', ' ')))
      }

      val initial = Board(Vector(
        Vector(' ', ' ', ' ', ' '),
        Vector(' ', ' ', ' ', ' '),
        Vector(' ', ' ', ' ', ' ')))

      val e = Updated(Set(
        Line(0, 0, 2, 0),
        Line(2, 0, 2, 2),
        Line(2, 2, 0, 2),
        Line(0, 2, 0, 0)
      ))
      val board: Board = initial.updated(e)

      board.grid shouldBe Vector(
        Vector('x', 'x', 'x', ' '),
        Vector('x', ' ', 'x', ' '),
        Vector('x', 'x', 'x', ' ')
      )
      linesDrown shouldBe Set(
        Line(0, 0, 2, 0),
        Line(2, 0, 2, 2),
        Line(2, 2, 0, 2),
        Line(0, 2, 0, 0)
      )
    }

    "be filled with color" in new Draw with TestFakes {

      val initial = Board(Vector(
        Vector(' ', ' ', ' ', ' ', ' ', ' ', ' '),
        Vector(' ', ' ', ' ', 'x', 'x', 'x', ' '),
        Vector('x', 'x', ' ', 'x', ' ', 'x', ' '),
        Vector(' ', 'x', ' ', 'x', 'x', 'x', ' '),
        Vector(' ', 'x', ' ', ' ', ' ', ' ', ' ')))

      var updated = false

      override def fill(x: Int, y: Int, c: Char)(implicit board: Board): Board = {
        updated = true
        Board(Vector(
          Vector(c  , c  , c  , c  , c  , c  , c  ),
          Vector(c  , c  , c  , 'x', 'x', 'x', c  ),
          Vector('x', 'x', c  , 'x', ' ', 'x', c  ),
          Vector(' ', 'x', c  , 'x', 'x', 'x', c  ),
          Vector(' ', 'x', c  , c  , c  , c  , c  )))
      }

      private val filledGrid = Vector(
        Vector('c', 'c', 'c', 'c', 'c', 'c', 'c'),
        Vector('c', 'c', 'c', 'x', 'x', 'x', 'c'),
        Vector('x', 'x', 'c', 'x', ' ', 'x', 'c'),
        Vector(' ', 'x', 'c', 'x', 'x', 'x', 'c'),
        Vector(' ', 'x', 'c', 'c', 'c', 'c', 'c'))

      initial.updated(Filled(2, 2, 'c')).grid shouldBe filledGrid

      updated shouldBe true
    }
  }
}
