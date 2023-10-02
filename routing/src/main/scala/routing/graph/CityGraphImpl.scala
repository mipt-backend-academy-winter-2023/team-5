package routing.graph

import repository.db.{Edges, Nodes}
import repository.model.{EdgeRow, NodeRow}
import zio.ZIO
import zio.prelude.data.Optional.AllValuesAreNullable
import zio.stream.ZStream

import scala.collection.mutable


object CityGraphImpl {
  def haversineDistance(p1: GeoPoint, p2: GeoPoint): Double = {
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

  def searchRoute(startId: Int, goalId: Int, graph: CityGraph): List[Node] = {
    val _nodes = graph.nodes.nodes
    val _edges = graph.edges.edges

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
        path.toList
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

  def loadNodes(): CityNodes = {
    var cityNodes = CityNodes(Map())
    Nodes.getAll().runCollect.map(_.toArray).either.flatMap {
      case Right(arr) => arr match {
        case Array() =>
          throw new Exception("No nodes")
        case _ =>
          cityNodes = CityNodes(arr.map(node => node.nodeType match {
            case 0 => (node.id, new House(node.id, GeoPoint(node.latitude, node.longitude), Some(node.name)))
            case _ => (node.id, new CrossRoad(node.id, GeoPoint(node.latitude, node.longitude), node.name))
          }).toMap)
          ZIO.succeed()
      }
      case Left(e) =>
        throw e
    }

    cityNodes
  }

  def loadGraph(): CityGraph = {
    val nodes = loadNodes()
    var cityEdges = CityEdges(Set())

    Edges.getAll().runCollect.map(_.toArray).either.flatMap {
      case Right(arr) => arr match {
        case Array() =>
          throw new Exception("No edges")
        case _ =>
          cityEdges = CityEdges(arr
            .map(edge => (nodes.nodes(edge.fromId), nodes.nodes(edge.toId), Road(edge.id, edge.name)))
            .toSet
          )
          ZIO.succeed()
      }
      case Left(e) =>
        throw e
    }

    CityGraph(nodes, cityEdges)
  }

}
