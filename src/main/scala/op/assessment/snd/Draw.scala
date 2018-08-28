package op.assessment.snd

trait Draw { self =>

  type Pixel
  type Grid = Vector[Vector[Pixel]]

  type Heights = Int
  type Width = Int

  val whitePixel: Pixel

  final case class Line(x1: Int, y1: Int, x2: Int, y2: Int)

  sealed trait Event
  final case class Created(heights: Int, width: Int) extends Event
  final case class LineAdded(line: Line) extends Event
  final case class Updated(lines: Set[Line]) extends Event
  final case class Filled(x :Int, y: Int, c: Pixel) extends Event

  def validate(cmd: String)(implicit board: Board): Option[Event]
  def draw(line: Line)(implicit board: Board): Board
  def fill(x: Int, y: Int, c: Pixel)(implicit board: Board): Board

  final case class Board private(grid: Grid) {

    implicit val board: Board = this

    val size: (Heights, Width) = (
      grid.size,
      grid.headOption.map(_.size).getOrElse(0))

    def apply(x: Int, y: Int): Pixel = grid(y)(x)

    def validate(cmd: String): Option[Event] = self.validate(cmd)

    def updated(e: Event): Board = e match {
      case Created(h, w) => copy(Vector.fill(h, w)(whitePixel))
      case LineAdded(line) => draw(line)
      case Filled(x, y, c) => fill(x, y, c)
      case Updated(lines) =>
        val drawLine = (ln: Line) => (b: Board) => draw(ln)(b)
        val drawFigure = lines.map(drawLine).reduce(_ andThen _)
        drawFigure(board)
    }
  }

  object Board {
    def empty: Board = Board(Vector.empty[Vector[Pixel]])
  }
}
