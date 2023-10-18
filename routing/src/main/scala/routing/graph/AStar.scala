package routing.graph
import map_repository.model.Point

import scala.collection.mutable

case class Node(point: Point, cost: Float, parent: Option[Node])

object AStar {
  def distance(a: Point, b: Point): Float = {
    val (x1, y1) = (a.x, a.y)
    val (x2, y2) = (b.x, b.y)
    Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)).toFloat
  }

  def aStar(
      start: Point,
      goal: Point,
      edges: Map[Point, List[Point]]
  ): List[Point] = {
    val openSet = mutable.PriorityQueue.empty[Node](
      Ordering.by[Node, Float](_.cost).reverse
    )
    openSet.enqueue(Node(start, 0, None))

    val closedSet = mutable.Set.empty[Point]

    while (openSet.nonEmpty) {
      val current = openSet.dequeue()

      if (current.point == goal) {
        // Reconstruct the path
        var path = List.empty[Point]
        var currentNode: Option[Node] = Some(current)
        while (currentNode.isDefined) {
          path = currentNode.get.point :: path
          currentNode = currentNode.get.parent
        }
        return path
      }

      closedSet += current.point

      for (neighborPoint <- edges.getOrElse(current.point, List.empty)) {
        if (!closedSet.contains(neighborPoint)) {
          val tentativeCost =
            current.cost + distance(current.point, neighborPoint)

          val neighborNode = Node(
            neighborPoint,
            tentativeCost + distance(neighborPoint, goal),
            Some(current)
          )

          openSet.enqueue(neighborNode)
        }
      }
    }

    null
  }
}
