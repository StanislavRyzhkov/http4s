package company.ryzhkov.repository

import cats.effect.IO
import company.ryzhkov.model.{Reply, Text}
import org.mongodb.scala.Completed
import org.mongodb.scala.result.UpdateResult

trait TextRepository {
  def save(text: Text): IO[Completed]
  def findByEnglishTitle(englishTitle: String): IO[Text]
  def findAllByKindSortedByCreatedDesc(kind: String): IO[Seq[Text]]
  def updateByEnglishTitle(
      englishTitle: String,
      replies: Seq[Reply]
  ): IO[UpdateResult]
}
