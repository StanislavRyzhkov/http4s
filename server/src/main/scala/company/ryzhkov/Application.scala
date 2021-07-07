package company.ryzhkov

import cats.effect._
import cats.implicits._
import company.ryzhkov.controller.{TextController, UserController}
import company.ryzhkov.db.{TextRepositoryImpl, UserRepositoryImpl}
import company.ryzhkov.service.{TextService, UserService}
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends IOApp {
  val textRepository = new TextRepositoryImpl()
  val userRepository = new UserRepositoryImpl()

  val userService = new UserService(userRepository)
  val textService = new TextService(textRepository, userService)

  val userEndPoint = new UserController(userService).endPoint
  val textEndPoint = new TextController(textService).endPoint

  val endPoint = textEndPoint <+> userEndPoint

  val httpApp = Router("/api" -> endPoint).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
