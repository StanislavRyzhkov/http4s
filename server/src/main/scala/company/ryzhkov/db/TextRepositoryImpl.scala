package company.ryzhkov.db

import cats.effect.IO
import company.ryzhkov.config.Mongo
import company.ryzhkov.model._
import company.ryzhkov.repository.TextRepository
import company.ryzhkov.util.ApplicationImplicits._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.{Completed, FindObservable, MongoCollection}

import scala.concurrent.ExecutionContext

class TextRepositoryImpl(implicit ec: ExecutionContext) extends TextRepository {
  val collection: MongoCollection[Text] = Mongo.textCollection

  override def save(text: Text): IO[Completed] = collection.insertOne(text)

  override def findOne(textFilter: TextFilter): IO[Option[Text]] = findObservable(textFilter)

  override def findMany(textFilter: TextFilter, sorting: TextSorting): IO[Seq[Text]] = {
    val res = findObservable(textFilter)
    sorting match { case Created => res.sort(descending("created")) }
  }

  private def findObservable(textFilter: TextFilter): FindObservable[Text] = textFilter match {
    case EnglishTitle(englishTitle) => collection.find(equal("englishTitle", englishTitle))
    case Kind(kind) => collection.find(equal("kind", kind))
  }
}
