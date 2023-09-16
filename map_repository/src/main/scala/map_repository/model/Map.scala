package map_repository.model

import org.locationtech.jts.geom.{LineString, Point}

case class Street(id: Int, name: String)
case class Point(id: Int, name: String, geom: Point)
case class Edge(id: Int, intersectionFrom: Int, intersectionTo: Int, streetId: Int, geom: LineString)
