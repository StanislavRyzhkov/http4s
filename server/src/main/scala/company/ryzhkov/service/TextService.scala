package company.ryzhkov.service

import cats.effect.IO
import company.ryzhkov.model.{
  CreateReply,
  Reply,
  ReplyStringDate,
  Text,
  TextFull,
  TextInfo
}
import company.ryzhkov.repository.TextRepository
import company.ryzhkov.util.DateFormatter

import java.util.Date

import company.ryzhkov.model._
import company.ryzhkov.repository.TextRepository

import org.mongodb.scala.result.UpdateResult

class TextService(textRepository: TextRepository) extends DateFormatter {

  def findAllArticles: IO[Seq[TextInfo]] =
    textRepository
      .findAllByKindSortedByCreatedDesc("ARTICLE")
      .map(_.map(toTextInfo))

  def findTwoLastArticles: IO[Seq[TextInfo]] =
    findAllArticles.map(_.slice(0, 2))

  def findFullTextByEnglishTitle(englishTitle: String): IO[Option[TextFull]] =
    textRepository
      .findByEnglishTitle(englishTitle)
      .map(_.map { text =>
        val Text(_id, created, title, englishTitle, _, components, replies) =
          text
        TextFull(
          _id.toHexString,
          dateToString(created),
          title,
          englishTitle,
          components,
          replies.map(toReplyStringDate)
        )
      })

  def createReply(
      optionHeader: Option[String],
      createReply: CreateReply
  ): IO[UpdateResult] = {
    val tuple = for {
      article <- textRepository findByEnglishTitle createReply.englishTitle
      user <- userService findUserByHeader optionHeader
    } yield {
      val reply = Reply(user.username, createReply.content, new Date())
      textRepository.updateByEnglishTitle(
        article.englishTitle,
        article.replies.+:(reply)
      )
    }
    tuple.flatten
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

  private def toReplyStringDate(e: Reply) =
    ReplyStringDate(e.username, e.content, dateToString(e.created))
}
