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
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Application extends IOApp {

  case class Hello(name: String)
  case class User(name: String)

  val hello = Hello("Stas")

  val f = Future(1)

  val g = IO.fromFuture(IO(f))



  implicit val helloDecoder: EntityDecoder[IO, Hello] = jsonOf[IO, Hello]

  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "hello" / name =>
          Ok(s"Hello, $name.")
        case GET -> Root / "hey" => Ok(hello.asJson)
        case req @ POST -> Root / "hello" =>
          val token = req.headers.find(e => e.name.toString() == "Authorization")
          val o = IO(req).map(e => e.headers.find(e => e.name.toString() == ""))
          val x = req.as[Hello]
          for {
            _ <- req.as[Hello]
            res <- Ok("")
          } yield res

        case req @ POST -> Root / "bar" =>
          for {
            a <- req.as[Hello]
            res <- Ok(a.name)
          } yield (res)
      }
      .orNotFound

//  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] =
//    HttpRoutes
//      .of[IO] {
//        case GET -> Root / "hello" / name =>
//          Ok(s"Hello, $name.")
//        case GET -> Root / "hey" => Ok(hello.asJson)
//        case req @ POST -> Root / "hello" =>
//          val f = req
//          for {
//            h <- f.as[Hello]
//            resp <- Ok("")
//          } yield (resp)
//
//        case req @ POST -> Root / "bar" =>
//          for {
//            a <- req.as[Hello]
//            res <- Ok(a.name)
//          } yield (res)
//      }
//      .orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(helloWorldService)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
