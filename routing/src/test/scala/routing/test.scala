package routing

import circuitbreaker.{JamValue, JamsIntegration, JamsIntegrationImpl, ZioCircuitBreakerImpl}
import map_repository.cache.MapInfo
import map_repository.db.{Edges, Points}
import map_repository.model.{Edge, Point}
import routing.api.HttpRoutes
import sttp.client3.httpclient.zio.HttpClientZioBackend
import zio._
import zio.http._
import zio.http.model._
import zio.stream.ZStream
import zio.test.Assertion._
import zio.test._

object SimpleHttpServer {
  val app: HttpApp[Any, Nothing] = Http.collect[Request] {
    case Method.GET -> !! / "hello" =>
      Response.text("Hello, ZIO!")
  }
}

class PointsMock extends Points {
  override def getAll() = ???
}

class EdgesMock extends Edges {
  override def getAll() = ???
}

class MapInfoMock(points: Array[Point], edges: Array[Edge])
    extends MapInfo.Service {
  override def getPoints: ZIO[Any, Throwable, Array[Point]] =
    ZIO.succeed(points)

  override def getEdges: ZIO[Any, Throwable, Array[Edge]] = ZIO.succeed(edges)
}

class JamsIntegrationMock() extends JamsIntegration {
  def getJam(id: Int): IO[Serializable, JamValue] = ZIO.succeed(JamValue(0))
}

object SimpleHttpServerTest extends ZIOSpecDefault {
  val spec = suite("Routing")(
    test("have edge") {
      val points = ZLayer.succeed(new PointsMock())
      val edges = ZLayer.succeed(new EdgesMock())
      val map_info = ZLayer.succeed(
        new MapInfoMock(
          Array[Point](Point(0, "biba", 0, 0), Point(1, "boba", 1, 1)),
          Array[Edge](Edge(0, 0, 1, 0))
        )
      )
      val jam = ZLayer.succeed(
        new JamsIntegrationMock
      )

      val request =
        Request.post(
          Body.fromString("[{\"value\": 0}, {\"value\": 1}]"),
          URL(!! / "route" / "search")
        )

      val appUnderTest = HttpRoutes.app

      (for {
        response <- appUnderTest.runZIO(request)
      } yield assert(response.status)(equalTo(Status.Ok)))
        .provide(points, edges, map_info, jam, ZioCircuitBreakerImpl.live, Scope.default, HttpClientZioBackend.layer())
    },
    test("haven't edge") {
      val points = ZLayer.succeed(new PointsMock())
      val edges = ZLayer.succeed(new EdgesMock())
      val map_info = ZLayer.succeed(
        new MapInfoMock(
          Array[Point](Point(0, "biba", 0, 0), Point(1, "boba", 1, 1)),
          Array[Edge]()
        )
      )
      val jam = ZLayer.succeed(
        new JamsIntegrationMock
      )

      val request =
        Request.post(
          Body.fromString("[{\"value\": 0}, {\"value\": 1}]"),
          URL(!! / "route" / "search")
        )

      val appUnderTest = HttpRoutes.app

      (for {
        response <- appUnderTest.runZIO(request)
      } yield assert(response.status)(not(equalTo(Status.Ok))))
        .provide(points, edges, map_info, jam, ZioCircuitBreakerImpl.live, Scope.default, HttpClientZioBackend.layer())
    }
  )
}
