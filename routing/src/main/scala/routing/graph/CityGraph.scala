package routing.graph

import repository.db.{Edges, Nodes}
import repository.model.{EdgeRow, NodeRow}
import zio.ZIO
import zio.stream.ZStream

import scala.collection.mutable

case class GeoPoint(latitude: Double, longitude: Double)

sealed abstract class Node(val id: Int, val point: GeoPoint, val name: Option[String])

class House(id: Int, point: GeoPoint, name: Option[String])
  extends Node(id, point, name)

class CrossRoad(id: Int, point: GeoPoint, name: String)
  extends Node(id, point, Some(name))

case class Road(id: Int, name: String)

case class CityNodes(nodes: Map[Int, Node])

case class CityEdges(edges: Set[(Node, Node, Road)])

case class CityGraph(nodes: CityNodes, edges: CityEdges)
