package company.ryzhkov.controller

import cats.data.Kleisli
import cats.effect._
import company.ryzhkov.model.CreateReply
import company.ryzhkov.service.TextService
import company.ryzhkov.util.HeaderReceiver
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{EntityDecoder, HttpRoutes, Request, Response}

class TextController(textService: TextService) extends HeaderReceiver {
  implicit val createReplyDecoder: EntityDecoder[IO, CreateReply] =
    jsonOf[IO, CreateReply]

  val endPoint: Kleisli[IO, Request[IO], Response[IO]] = HttpRoutes
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
        val authHeader = transform(req)
        req
          .as[CreateReply]
          .flatMap(
            createReply => textService.createReply(authHeader, createReply)
          )
          .flatMap(_ => Ok())
          .handleErrorWith(e => BadRequest(e.getMessage))
    }
    .orNotFound
}
