package company.ryzhkov.controller

import cats.data.Kleisli
import cats.effect._
import company.ryzhkov.model.Register
import company.ryzhkov.service.UserService
import company.ryzhkov.util.HeaderReceiver
import io.circe.generic.auto._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}

class UserController(userService: UserService) extends HeaderReceiver {
  implicit val registerDecoder: EntityDecoder[IO, Register] =
    jsonOf[IO, Register]

  val endPoint: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case req @ POST -> Root / "api" / "register" =>
          req
            .as[Register]
            .flatMap(userService.register)
            .flatMap(_ => Ok())
            .handleErrorWith(e => BadRequest(e.getMessage))
      }
      .orNotFound
}
