import auth.AuthMain
import helper.HelperMain
import routing.RoutingMain
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object StartApp extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      auth <- AuthMain.run.fork
      routing <- RoutingMain.run.fork
      _ <- auth.join *> routing.join *> ZIO.never
    } yield ()
}
