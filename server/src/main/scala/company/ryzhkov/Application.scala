package company.ryzhkov

import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import company.ryzhkov.controller.UserController
import company.ryzhkov.db.{TextRepositoryImpl, UserRepositoryImpl}
import company.ryzhkov.model.{CreateReply, TextInfo}
import company.ryzhkov.service.{TextService, UserService}
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.blaze._
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response, Status}
import io.circe.generic.auto._
//import org.http4s.circe.CirceEntityEncoder._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Application extends IOApp {

  case class Hello(name: String)
  case class User(name: String)

  val hello = Hello("Stas")

  implicit val helloDecoder: EntityDecoder[IO, Hello] = jsonOf[IO, Hello]
  implicit val createReplyDecoder: EntityDecoder[IO, CreateReply] =
    jsonOf[IO, CreateReply]
//  implicit val textInfoDecoder: EntityDecoder[IO, TextInfo] = jsonOf[IO, TextInfo]
//  implicit val textInfoSeqDecoder: EntityDecoder[IO, Seq[TextInfo]] = jsonOf[IO, Seq[TextInfo]]

  val textRepository = new TextRepositoryImpl()
  val userRepository = new UserRepositoryImpl()

  val userService = new UserService(userRepository)
  val textService = new TextService(textRepository, userService)

  val userController = new UserController(userService)

//  implicit def format(articles: Seq[TextInfo]): Json = articles.asJson

  val textController: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "api" / "articles" / "all" =>
          textService.findAllArticles.flatMap(e => Ok(e.asJson))

        case GET -> Root / "api" / "articles" / "two" =>
          textService.findTwoLastArticles.flatMap(e => Ok(e.asJson))

        case GET -> Root / "api" / "articles" / "detail" / engTitle =>
          textService
            .findFullTextByEnglishTitle(engTitle)
            .flatMap(e => Ok(e.asJson))
            .handleErrorWith(_ => NotFound())

        case req @ POST -> Root / "api" / "articles" / "reply" =>
          val auth =
            req.headers.find(_.name.toString() == "Authorization").map(_.value)
          req
            .as[CreateReply]
            .flatMap(e => textService.createReply(auth, e))
            .flatMap(_ => Ok())
            .handleErrorWith(e => BadRequest(e.getMessage))
//              .handleError(e => Response(status = BadRequest))

//            for {
//              a <- req.as[CreateReply]
//              _ <- textService.createReply(auth, a)
//              b <- Ok()
//            } yield b
      }
      .orNotFound

  val helloWorldService: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case GET -> Root / "hello" / name =>
          Ok(s"Hello, $name.")
        case GET -> Root / "hey" => Ok(hello.asJson)
        case req @ POST -> Root / "hello" =>
          val token =
            req.headers.find(e => e.name.toString() == "Authorization")
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
      .withHttpApp(textController)
      .withHttpApp(userController.endPoint)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
