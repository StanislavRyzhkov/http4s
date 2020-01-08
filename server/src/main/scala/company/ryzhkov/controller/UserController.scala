package company.ryzhkov.controller

import cats.effect._
import cats.implicits._
import company.ryzhkov.model.{Auth, Message, Register}
import company.ryzhkov.service.UserService
import company.ryzhkov.util.HeaderReceiver
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, HttpRoutes, Response}

class UserController(userService: UserService) extends HeaderReceiver {
  implicit val registerDecoder: EntityDecoder[IO, Register] =
    jsonOf[IO, Register]
  implicit val authDecoder: EntityDecoder[IO, Auth] = jsonOf[IO, Auth]

  val endPoint: HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case req @ POST -> Root / "users" / "register" =>
          (for {
            register <- req.as[Register]
            validRegister <- register.validate
            _ <- userService.register(validRegister)
            result <- Ok(Message("Успешная регистрация").asJson)
          } yield result)
            .handleErrorWith(e => BadRequest(Message(e.getMessage).asJson))

        case req @ POST -> Root / "users" / "auth" =>
          (for {
            auth <- req.as[Auth]
            token <- userService authenticate auth
            result <- Ok(Message(token).asJson)
          } yield result)
            .handleErrorWith(e => BadRequest(Message(e.getMessage).asJson))

        case req @ GET -> Root / "users" / "user_area" / "username" =>
          (for {
            optionHeader <- transform(req)
            username <- userService findUsernameByHeader optionHeader
            result <- Ok(Message(username).asJson)
          } yield result).handleError(_ => Response(Unauthorized))
      }
}
