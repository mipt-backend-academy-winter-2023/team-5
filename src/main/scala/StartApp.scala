import auth.AuthMain
import helper.HelperMain
import routing.RoutingMain
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object StartApp extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] =
    for {
      routing <- RoutingMain.run.fork
      helper <- HelperMain.run.fork
      auth <- AuthMain.run.fork
      _ <- helper.join
      _ <- auth.join
      _ <- routing.join
    } yield ()
}
