package company.ryzhkov.db

import cats.effect.IO
import company.ryzhkov.config.Mongo
import company.ryzhkov.model._
import company.ryzhkov.repository.TextRepository
import company.ryzhkov.util.Constants.ObjectNotFound
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.{Completed, MongoCollection}

import scala.concurrent.{ExecutionContext, Future}

class TextRepositoryImpl(implicit ec: ExecutionContext) extends TextRepository {
  val collection: MongoCollection[Text] = Mongo.textCollection

  implicit def toIO[A](future: Future[A]): IO[A] = IO.fromFuture(IO(future))

  override def save(text: Text): IO[Completed] =
    collection.insertOne(text).toFuture()

  override def updateByEnglishTitle(
      englishTitle: String,
      replies: Seq[Reply]
  ): IO[UpdateResult] =
    collection
      .updateOne(equal("englishTitle", englishTitle), set("replies", replies))
      .toFuture()

  override def findAllByKindSortedByCreatedDesc(
      kind: String
  ): IO[Seq[Text]] =
    collection
      .find(equal("kind", kind))
      .sort(descending("created"))
      .toFuture()

  override def findByEnglishTitle(englishTitle: String): IO[Text] =
    collection
      .find(equal("englishTitle", englishTitle))
      .head()
      .map(e => if (e == null) throw new Exception(ObjectNotFound) else e)
}
