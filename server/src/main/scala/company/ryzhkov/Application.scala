package company.ryzhkov

import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}

object Application extends IOApp {

  case class Hello(name: String)
  case class User(name: String)

  val hello = Hello("Stas")

  implicit val helloDecoder: EntityDecoder[IO, Hello] = jsonOf[IO, Hello]

  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "hello" / name =>
          Ok(s"Hello, $name.")
        case GET -> Root / "hey" => Ok(hello.asJson)
        case req @ POST -> Root / "hello" =>
          for {
            h <- req.as[Hello]
            resp <- Ok(h.name)
          } yield (resp)

        case req @ POST -> Root / "bar" =>
          for {
            a <- req.as[Hello]
            res <- Ok(a.name)
          } yield (res)
      }
      .orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
