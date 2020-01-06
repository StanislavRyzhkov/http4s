package company.ryzhkov.service

import cats.effect.IO
import company.ryzhkov.model.{Text, TextInfo}
import company.ryzhkov.repository.TextRepository
import company.ryzhkov.util.DateFormatter

class TextService(textRepository: TextRepository) extends DateFormatter {

  def findAllArticles: IO[Seq[TextInfo]] =
    textRepository
      .findAllByKindSortedByCreatedDesc("ARTICLE")
      .map(_.map(toTextInfo))


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
}
