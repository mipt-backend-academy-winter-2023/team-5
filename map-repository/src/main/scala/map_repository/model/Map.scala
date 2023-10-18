package map_repository.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class Street(id: Int, name: String)
case class Point(id: Int, name: String, x: Float, y: Float)
object Point {
  implicit val decoder: JsonDecoder[Point] =
    DeriveJsonDecoder.gen[Point]
  implicit val encoder: JsonEncoder[Point] =
    DeriveJsonEncoder.gen[Point]
}
case class Edge(
    id: Int,
    pointFrom: Int,
    pointTo: Int,
    streetId: Int
)
