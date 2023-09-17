package map_repository.model

case class Street(id: Int, name: String)
case class Point(id: Int, name: String, x: Float, y: Float)
case class Edge(
    id: Int,
    pointFrom: Int,
    pointTo: Int,
    streetId: Int,
)
