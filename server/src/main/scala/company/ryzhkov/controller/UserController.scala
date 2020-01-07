package company.ryzhkov.controller

import cats.data.Kleisli
import cats.effect._
import company.ryzhkov.model.{Auth, Message, Register}
import company.ryzhkov.service.UserService
import company.ryzhkov.util.HeaderReceiver
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}

class UserController(userService: UserService) extends HeaderReceiver {
  implicit val registerDecoder: EntityDecoder[IO, Register] =
    jsonOf[IO, Register]
  implicit val authDecoder: EntityDecoder[IO, Auth] = jsonOf[IO, Auth]

  val endPoint: Kleisli[IO, Request[IO], Response[IO]] =
    HttpRoutes
      .of[IO] {
        case req @ POST -> Root / "api" / "register" =>
          req
            .as[Register]
            .map(_.validate)
            .flatMap(userService.register)
            .flatMap(_ => Ok(Message("Успешая регистрация").asJson))
            .handleErrorWith(e => BadRequest(Message(e.getMessage).asJson))

        case req @ POST -> Root / "api" / "auth" =>
          req
            .as[Auth]
            .flatMap(userService.authenticate)
            .flatMap(_ => Ok())
            .handleErrorWith(e => BadRequest(Message(e.getMessage).asJson))
      }
      .orNotFound
}
