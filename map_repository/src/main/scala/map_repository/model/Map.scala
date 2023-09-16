package map_repository.model

import org.locationtech.jts.geom

case class Street(id: Int, name: String)
case class Point(id: Int, name: String, geom: geom.Point)
case class Edge(
    id: Int,
    intersectionFrom: Int,
    intersectionTo: Int,
    streetId: Int,
    geom: geom.LineString
)
