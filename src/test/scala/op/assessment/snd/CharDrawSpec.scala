package op.assessment.snd

import org.scalatest.FunSuite

class CharDrawSpec extends FunSuite {

  import CharDraw._

  val board = Board(Vector(
    Vector(' ', ' ', ' ', ' '),
    Vector(' ', ' ', ' ', ' '),
    Vector(' ', ' ', ' ', ' '),
    Vector(' ', ' ', ' ', ' ')))

  test("validate invalid command") {
    assert(validate("Invalid command")(board) === None)
  }

  test("valid 'C 3 4' command") {
    assert(validate("C 3 4")(board) === Some(
      Created(heights = 4, width = 3)
    ))
  }

  test("validate 'bucket fill' command") {
    val board = Board(Vector(
      Vector(' ', ' ', ' ', ' '),
      Vector('x', 'x', ' ', ' '),
      Vector(' ', 'x', ' ', ' '),
      Vector(' ', 'x', ' ', ' ')))

    assert(validate("B -1  2 c")(board) === None)
    assert(validate("B  1 -2 c")(board) === None)
    assert(validate("B  2  4 c")(board) === None)
    assert(validate("B  5  2 c")(board) === None)
    assert(validate("B  1  1 c")(board) === None)
    assert(validate("B  1  2 c")(board) === None)
    assert(validate("B  2  2 c")(board) === Some(Filled(2, 2, 'c')))
  }

  test("valid horizontal line 'L 0 1 2 1' command") {
    assert(validate("L 0 1 2 1")(board) === Some(
      LineAdded(Line(0, 1, 2, 1))
    ))
  }

  test("valid vertical line 'L 2 1 2 2' command") {
    assert(validate("L 2 1 2 2")(board) === Some(
      LineAdded(Line(2, 1, 2, 2))
    ))
  }

  test("invalid line 'L 0 1 2 2' command") {
    assert(validate("L 0 1 2 2")(board) === None)
  }

  test("validate cross the board horizontal line 'L -1 1 2 1' command") {
    assert(validate("L -1 1 2 1")(board) === Some(
      LineAdded(Line(0, 1, 2, 1))
    ))
  }

  test("validate cross the board horizontal line 'L 1 1 4 1' command") {
    assert(validate("L 1 1 4 1")(board) === Some(
      LineAdded(Line(1, 1, 3, 1))
    ))
  }

  test("validate cross the board vertical line 'L 1 -1 1 2' command") {
    assert(validate("L 1 -1 1 2")(board) === Some(
      LineAdded(Line(1, 0, 1, 2))
    ))
  }

  test("validate cross the board vertical line 'L 2 2 2 4' command") {
    assert(validate("L 2 2 2 4")(board) === Some(
      LineAdded(Line(2, 2, 2, 3))
    ))
  }

  test("validate out the board line command") {
    assert(validate("L 2 -2 3 -2")(board) === None)
    assert(validate("L 2 4 3 4")(board) === None)
    assert(validate("L 1 -1 1 -2")(board) === None)
    assert(validate("L 4 1 4 2")(board) === None)
  }

  test("valid 'R 1 1 2 2' command") {
    assert(validate("R 1 1 2 2")(board) === Some(
      Updated(Set(
        Line(1, 1, 2, 1),
        Line(2, 1, 2, 2),
        Line(2, 2, 1, 2),
        Line(1, 2, 1, 1)
      ))
    ))
  }

  test("valid our of the board rectangle command") {
    assert(validate("R 1 -4 2 -2")(board) === None)
    assert(validate("R -4 1 -2 2")(board) === None)
    assert(validate("R 1 6 2 5")(board) === None)
    assert(validate("R 5 1 7 2")(board) === None)
  }

  test("validate cross the board 'R -1 1 2 2' command") {
    assert(validate("R -1 1 2 2")(board) === Some(
      Updated(Set(
        Line(0, 1, 2, 1),
        Line(2, 1, 2, 2),
        Line(2, 2, 0, 2)
      ))
    ))
  }

  test("validate cross the board 'R 1 1 4 2' command") {
    assert(validate("R  1 1 4 2")(board) === Some(
      Updated(Set(Line(1, 1, 3, 1), Line(1, 2, 1, 1), Line(3, 2, 1, 2)))
    ))
  }

  test("validate cross the board 'R 1 -1 2 2' command") {
    assert(validate("R 1 -1 2 2")(board) ===
      Some(Updated(Set(Line(2,0,2,2), Line(2,2,1,2), Line(1,2,1,0)))))
  }

  test("validate cross the board 'R 1 1 2 4' command") {
    assert(validate("R 1 1 2 4")(board) ===
      Some(Updated(Set(Line(1,1,2,1), Line(2,1,2,3), Line(1,3,1,1)))))
  }

  test("validate multi-spaces valid case") {
    assert(validate("R    1  1 2   2")(board) === Some(
      Updated(Set(
        Line(1, 1, 2, 1),
        Line(2, 1, 2, 2),
        Line(2, 2, 1, 2),
        Line(1, 2, 1, 1)))
    ))
  }

  test("draw line") {

    assert(draw(Line(1, 1, 1, 1))(board).grid === Vector(
      Vector(' ', ' ', ' ', ' '),
      Vector(' ', 'x', ' ', ' '),
      Vector(' ', ' ', ' ', ' '),
      Vector(' ', ' ', ' ', ' '))
    )

    assert(draw(Line(1, 2, 3, 2))(board).grid === Vector(
      Vector(' ', ' ', ' ', ' '),
      Vector(' ', ' ', ' ', ' '),
      Vector(' ', 'x', 'x', 'x'),
      Vector(' ', ' ', ' ', ' '))
    )

    assert(draw(Line(2, 1, 2, 3))(board).grid === Vector(
      Vector(' ', ' ', ' ', ' '),
      Vector(' ', ' ', 'x', ' '),
      Vector(' ', ' ', 'x', ' '),
      Vector(' ', ' ', 'x', ' '))
    )
  }

  test("fill") {

    val b = Board(Vector(
      Vector(' ', ' ', ' ', ' ', ' ', ' ', ' '),
      Vector(' ', ' ', ' ', 'x', 'x', 'x', ' '),
      Vector('x', 'x', ' ', 'x', ' ', 'x', ' '),
      Vector(' ', 'x', ' ', 'x', 'x', 'x', ' '),
      Vector(' ', 'x', ' ', ' ', ' ', ' ', ' ')))

    assert(fill(x = 4, y = 2, c = 'c')(b) == Board(Vector(
      Vector(' ', ' ', ' ', ' ', ' ', ' ', ' '),
      Vector(' ', ' ', ' ', 'x', 'x', 'x', ' '),
      Vector('x', 'x', ' ', 'x', 'c', 'x', ' '),
      Vector(' ', 'x', ' ', 'x', 'x', 'x', ' '),
      Vector(' ', 'x', ' ', ' ', ' ', ' ', ' '))))

    assert(fill(x = 2, y = 2, c = 'c')(b) == Board(Vector(
      Vector('c', 'c', 'c', 'c', 'c', 'c', 'c'),
      Vector('c', 'c', 'c', 'x', 'x', 'x', 'c'),
      Vector('x', 'x', 'c', 'x', ' ', 'x', 'c'),
      Vector(' ', 'x', 'c', 'x', 'x', 'x', 'c'),
      Vector(' ', 'x', 'c', 'c', 'c', 'c', 'c'))))
  }
}
