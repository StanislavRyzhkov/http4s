package company.ryzhkov.repository

import cats.effect.IO
import company.ryzhkov.model.{Reply, Text, TextFilter, TextSorting}
import org.mongodb.scala.Completed
import org.mongodb.scala.result.UpdateResult

trait TextRepository {
  def save(text: Text): IO[Completed]
  def findOne(textFilter: TextFilter): IO[Option[Text]]
  def findMany(textFilter: TextFilter, sorting: TextSorting): IO[Seq[Text]]

//  def updateByEnglishTitle(
//      englishTitle: String,
//      replies: Seq[Reply]
//  ): IO[UpdateResult]
}
