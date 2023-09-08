import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.IpLiteralSyntax
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router

object Main extends IOApp {
  object dsl extends Http4sDsl[IO]
  import dsl._
  val router = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Hello world!")
  }
  val httpApp = Router("/" -> router).orNotFound
  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .useForever
      .as(ExitCode.Success)
}