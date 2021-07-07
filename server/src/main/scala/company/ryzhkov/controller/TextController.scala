package company.ryzhkov.controller

import cats.effect._
import company.ryzhkov.model.{CreateReply, Message}
import company.ryzhkov.service.TextService
import company.ryzhkov.util.HeaderReceiver
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, HttpRoutes}

class TextController(textService: TextService) extends HeaderReceiver {
  implicit val createReplyDecoder: EntityDecoder[IO, CreateReply] =
    jsonOf[IO, CreateReply]

  val endPoint: HttpRoutes[IO] = HttpRoutes
    .of[IO] {
      case GET -> Root / "articles" / "all" =>
        textService.findAllArticles.flatMap(articles => Ok(articles.asJson))

      case GET -> Root / "articles" / "two" =>
        textService.findTwoLastArticles.flatMap(articles => Ok(articles.asJson))

      case GET -> Root / "articles" / "detail" / engTitle =>
        (for {
          text   <- textService findFullTextByEnglishTitle engTitle
          result <- Ok(text.asJson)
        } yield result).handleErrorWith(_ => NotFound())

      case req @ POST -> Root / "articles" / "reply" =>
        (for {
          authHeader <- transform(req)
          reply      <- req.as[CreateReply]
          validReply <- reply.validate
          _          <- textService.createReply(authHeader, validReply)
          result     <- Ok(Message("Комментарий создан").asJson)
        } yield result).handleErrorWith(e => BadRequest(e.getMessage))
    }
}
