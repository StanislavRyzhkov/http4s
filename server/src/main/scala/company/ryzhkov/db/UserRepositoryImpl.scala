package company.ryzhkov.db

import cats.effect.IO
import company.ryzhkov.config.Mongo
import company.ryzhkov.model._
import company.ryzhkov.repository.UserRepository
import company.ryzhkov.util.ApplicationImplicits._
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.{Completed, MongoCollection}

import scala.concurrent.ExecutionContext

class UserRepositoryImpl(implicit ec: ExecutionContext) extends UserRepository {
  val collection: MongoCollection[User] = Mongo.userCollection

  override def save(user: User): IO[Completed] = collection.insertOne(user)

  override def findOne(filter: UserFilter): IO[Option[User]] = filter match {
    case Username(username) =>
      collection
        .find(equal("username", username))

    case UsernameAndStatus(username, status) =>
      collection
        .find(and(equal("username", username), equal("status", status)))

    case Email(email) =>
      collection
        .find(equal("email", email))
  }
}
