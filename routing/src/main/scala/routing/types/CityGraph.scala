package routing.types

import scala.collection.mutable

case class GeoPoint(latitude: Double, longitude: Double)

abstract class Node(val id: Int, val point: GeoPoint)

class House(id: Int, point: GeoPoint, val address: String, val numberOfFloors: Int, val name: Option[String]) extends Node(id, point)

class CrossRoad(id: Int, point: GeoPoint, val crossRoadName: String, val trafficLight: Boolean) extends Node(id, point)

case class Road(id: Int, name: String)

class CityGraph {
  private var _nodes: Map[Int, Node] = Map()
  private var _edges: Set[(Node, Node, Road)] = Set()

  private def haversineDistance(p1: GeoPoint, p2: GeoPoint): Double = {
    val R = 6371000
    val dLat = (p2.latitude - p1.latitude).toRadians
    val dLon = (p2.longitude - p1.longitude).toRadians
    val lat1 = p1.latitude.toRadians
    val lat2 = p2.latitude.toRadians

    val a = math.sin(dLat / 2) * math.sin(dLat / 2) +
      math.sin(dLon / 2) * math.sin(dLon / 2) * math.cos(lat1) * math.cos(lat2)
    val c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    R * c
  }

  def searchRoute(startId: Int, goalId: Int): List[Node] = {
    if (!_nodes.contains(startId) || !_nodes.contains(goalId)) {
      return Nil
    }

    val start = _nodes(startId)
    val goal = _nodes(goalId)

    def heuristic(node: Node): Double = haversineDistance(node.point, goal.point)

    val openSet = mutable.PriorityQueue.empty[(Double, Node)](Ordering.by { (pair: (Double, Node)) => pair._1 }.reverse)
    openSet.enqueue((heuristic(start), start))

    val cameFrom = mutable.Map[Node, Node]()
    val gScore = mutable.Map[Node, Double](start -> 0)
    val fScore = mutable.Map[Node, Double](start -> heuristic(start))

    while (openSet.nonEmpty) {
      val current = openSet.dequeue()._2
      if (current.id == goal.id) {
        val path = mutable.ListBuffer[Node](current)
        var tmp = current
        while (cameFrom.contains(tmp)) {
          tmp = cameFrom(tmp)
          path.prepend(tmp)
        }
        return path.toList
      }

      for (edge <- _edges.filter(e => e._1 == current || e._2 == current)) {
        val neighbor = if (edge._1 == current) edge._2 else edge._1
        val tentativeGScore = gScore(current) + haversineDistance(current.point, neighbor.point)
        if (!gScore.contains(neighbor) || tentativeGScore < gScore(neighbor)) {
          cameFrom(neighbor) = current
          gScore(neighbor) = tentativeGScore
          fScore(neighbor) = tentativeGScore + heuristic(neighbor)
          if (!openSet.exists(_._2.id == neighbor.id)) {
            openSet.enqueue((fScore(neighbor), neighbor))
          }
        }
      }
    }

    // No path found
    Nil
  }

  def nodes: Set[Node] = _nodes.values.toSet

  def edges: Set[(Node, Node, Road)] = _edges

  def addNode(node: Node): Unit = {
    _nodes = _nodes + (node.id -> node)
  }

  def addEdge(from: Node, to: Node, road: Road): Unit = {
    _edges = _edges + ((from, to, road))
  }
}
