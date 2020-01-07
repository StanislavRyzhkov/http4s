package company.ryzhkov.service

import java.util.Date

import cats.effect.IO
import company.ryzhkov.model._
import company.ryzhkov.repository.TextRepository
import company.ryzhkov.util.Constants.AccessDenied
import company.ryzhkov.util.DateFormatter
import org.mongodb.scala.result.UpdateResult

class TextService(textRepository: TextRepository, userService: UserService)
    extends DateFormatter {

  def findAllArticles: IO[Seq[TextInfo]] =
    textRepository
      .findAllByKindSortedByCreatedDesc("ARTICLE")
      .map(_.map(toTextInfo))

  def findTwoLastArticles: IO[Seq[TextInfo]] =
    findAllArticles.map(_.slice(0, 2))

  def findFullTextByEnglishTitle(value: String): IO[TextFull] =
    textRepository.findByEnglishTitle(value).map(toTextFull)

  def createReply(
      optionHeader: Option[String],
      createReply: CreateReply
  ): IO[UpdateResult] = {
    val result = for {
      text <- textRepository.findByEnglishTitle(createReply.englishTitle)
      user <- userService.findUserByHeader(optionHeader)
    } yield {
      val reply = Reply(user.username, createReply.content, new Date())
      textRepository.updateByEnglishTitle(
        text.englishTitle,
        text.replies.+:(reply)
      )
    }
    result
      .flatMap(identity)
      .handleErrorWith(_ => IO.raiseError(new Exception(AccessDenied)))
  }

  private def toTextInfo(text: Text): TextInfo = {
    val Text(_id, created, title, englishTitle, _, components, _) = text
    TextInfo(
      _id.toHexString,
      components(1).source,
      components(3).content,
      title,
      englishTitle,
      dateToString(created)
    )
  }

  private def toTextFull(text: Text): TextFull = {
    val Text(_id, created, title, englishTitle, _, components, replies) = text
    TextFull(
      _id.toHexString,
      dateToString(created),
      title,
      englishTitle,
      components,
      replies.map(toReplyStringDate)
    )
  }

  private def toReplyStringDate(e: Reply) =
    ReplyStringDate(e.username, e.content, dateToString(e.created))
}
