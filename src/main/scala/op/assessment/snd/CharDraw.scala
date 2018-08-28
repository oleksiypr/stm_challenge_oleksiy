package op.assessment.snd

import scala.annotation.tailrec
import scala.collection.immutable.Queue
import scala.util.Try

object CharDraw extends Draw {

  override type Pixel = Char

  override val whitePixel: Pixel = ' '

  sealed trait Command
  final case class Create(width: Int, heights: Int) extends Command
  final case class AddLine(x1: Int, y1: Int, x2: Int, y2: Int) extends Command
  final case class AddRectangle(x1: Int, y1: Int, x2: Int, y2: Int) extends Command
  final case class BucketFill(x: Int, y: Int, c: Pixel) extends Command

  object Command {

    def apply(cmd: String): Command = cmd.split(" ").filter(_.nonEmpty) match {
      case Array("C", width, heights) =>
        Create(width.toInt, heights.toInt)
      case Array("L", x1, y1, x2, y2) =>
        AddLine(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
      case Array("R", x1, y1, x2, y2) =>
        AddRectangle(x1.toInt, y1.toInt, x2.toInt, y2.toInt)
      case Array("B", x, y, c) => BucketFill(x.toInt, y.toInt, c.charAt(0))
    }

    def unapply(cmd: String) : Option[Command] = Try(Command(cmd)).toOption
  }


  override def draw(line: Line)(implicit board: Board): Board = {
    import math._
    val array = board.grid.toArray.map(_.toArray)
    val (xBegin, xEnd) = (min(line.x1, line.x2), max(line.x1, line.x2))
    val (yBegin, yEnd) = (min(line.y1, line.y2), max(line.y1, line.y2))
    for {
      x <- xBegin to xEnd
      y <- yBegin to yEnd
    } {
      array(y)(x) = 'x'
    }
    Board(array.toVector.map(_.toVector))
  }

  override def validate(cmd: String)(
      implicit board: Board): Option[Event] = cmd match {

    case Command(Create(w, h)) =>  Some(Created(h, w))

    case Command(AddLine(x1, y1, x2, y2)) if isValid(x1, y1, x2, y2) =>
      val line = adjust(Line(x1, y1, x2, y2))
      Some(LineAdded(line))

    case Command(AddRectangle(x1, y1, x2, y2)) =>
      def validLine(line: Line): Boolean = isValid(line)
      def adjustWithBoard(line: Line): Line = adjust(line)
      val lines = Set(
        Line(x1, y1, x2, y1),
        Line(x2, y1, x2, y2),
        Line(x2, y2, x1, y2),
        Line(x1, y2, x1, y1)
      ).filter(validLine).map(adjustWithBoard)
      if (lines.isEmpty) None else Some(Updated(lines))

    case Command(BucketFill(x, y, c)) if isInside(x, y) && isFree(x, y) =>
      Some(Filled(x, y, c))

    case _ => None
  }

  override def fill(x: Width, y: Width, c: Char)(
      implicit board: Board): Board = {

    type Point = (Int, Int)
    def isInsideBord(p: Point): Boolean = isInside(p._1, p._2)

    @tailrec
    def bfs(grid: Array[Array[Pixel]])(
        queue: Queue[Point]): Array[Array[Pixel]] = {

      def isWhite(p: Point): Boolean = grid(p._2)(p._1) == whitePixel
      def isValid(p: Point): Boolean = isInsideBord(p) && isWhite(p)
      def color(p: Point): Unit = {
        val (i, j) = p.swap
        grid(i)(j) = c
      }
      def neighbours(p: Point): Set[Point] = {
        val (x, y) = p
        Set((x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)).filter(isValid)
      }

      queue.dequeueOption match {
        case None => grid
        case Some((p, remain)) =>
          val ns = neighbours(p)
          ns foreach color
          bfs(grid)(ns.foldLeft(remain)(_ enqueue _))
      }
    }

    val grid = board.grid.toArray.map(_.toArray)
    grid(y)(x) = c
    val filledGrid = bfs(grid)(Queue((x, y)))
    board.copy(grid = filledGrid.toVector.map(_.toVector))
  }

  private def isFree(x: Width, y: Width)(
      implicit board: CharDraw.Board): Boolean = board(x, y) == whitePixel

  private def isValid(line: Line)(
      implicit b: Board
    ): Boolean = isValid(line.x1, line.y1, line.x2, line.y2)(b)

  private def isValid(
      x1: Width, y1: Width, x2: Width, y2: Width)(
      implicit b: Board): Boolean = {

    def isCrossing: Boolean = isInside(x1, y1) || isInside(x2, y2)
    def isHorizontal: Boolean =  x1 == x2
    def isVertical: Boolean = y1 == y2

    isCrossing && (isHorizontal || isVertical)
  }

  private def isInside(x: Int, y: Int)(implicit b: Board): Boolean = {
    val (h, w) = b.size
    x >= 0 && x < w && y >= 0 && y < h
  }

  private def adjust(line: Line)(implicit b: Board): Line = {
    val (h, w) = b.size
    Line(
      adjust(line.x1)(w), adjust(line.y1)(h),
      adjust(line.x2)(w), adjust(line.y2)(h))
  }

  private def adjust(d: Int)(limit: Int): Int = {
    if (d < 0) 0 else
    if (d >= limit) limit - 1 else d
  }
}
