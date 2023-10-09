import auth.AuthMain
import routing.RoutingMain
import images.ImagesMain
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object StartApp extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      auth <- AuthMain.run.fork
      routing <- RoutingMain.run.fork
      _ <- auth.join *> routing.join *> ZIO.never
      images <- ImagesMain.run.fork
      _ <- routing.join
      _ <- images.join
    } yield ()
}
