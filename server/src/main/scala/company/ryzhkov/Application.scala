package company.ryzhkov

import cats.effect._
import cats.implicits._
import company.ryzhkov.controller.{TextController, UserController}
import company.ryzhkov.db.{TextRepositoryImpl, UserRepositoryImpl}
import company.ryzhkov.service.{TextService, UserService}
import org.http4s.server.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends IOApp {
  val textRepository = new TextRepositoryImpl()
  val userRepository = new UserRepositoryImpl()

  val userService = new UserService(userRepository)
  val textService = new TextService(textRepository, userService)

  val userController = new UserController(userService)
  val textController = new TextController(textService)

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(8080, "localhost")
      .withHttpApp(textController.endPoint)
      .withHttpApp(userController.endPoint)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
