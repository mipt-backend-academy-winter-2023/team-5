package routing.graph

import repository.model.{NodeRow, EdgeRow}
import zio.ZIO
import zio.stream.ZStream

import scala.collection.mutable

case class GeoPoint(latitude: Double, longitude: Double)

sealed abstract class Node(val id: Int, val point: GeoPoint)

class House(id: Int, point: GeoPoint, val name: Option[String])
  extends Node(id, point)

class CrossRoad(id: Int, point: GeoPoint, val crossRoadName: String)
  extends Node(id, point)

case class Road(id: Int, name: String)

trait CityGraph {
  def searchRoute(startId: Int, goalId: Int): List[Node]

  def loadNodes(nodeStream: ZStream[Any, Throwable, NodeRow]): ZIO[Any, Throwable, Unit]

  def loadEdges(edgeStream: ZStream[Any, Throwable, EdgeRow]): ZIO[Any, Throwable, Unit]

}
