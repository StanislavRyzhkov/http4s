package company.ryzhkov.db

import cats.effect.IO
import company.ryzhkov.config.Mongo
import company.ryzhkov.model.User
import company.ryzhkov.repository.UserRepository
import company.ryzhkov.util.ApplicationImplicits._
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.result.UpdateResult
import company.ryzhkov.util.Constants.ObjectNotFound
import org.mongodb.scala.{Completed, MongoCollection}

import scala.concurrent.ExecutionContext

class UserRepositoryImpl(implicit ec: ExecutionContext) extends UserRepository {
  val collection: MongoCollection[User] = Mongo.userCollection

  override def save(user: User): IO[Completed] =
    collection.insertOne(user).toFuture()

  override def findByUsername(username: String): IO[User] =
    collection
      .find(equal("username", username))
      .head()
      .map(e => if (e == null) throw new Exception(ObjectNotFound) else e)

  override def findByUsernameAndStatus(
      username: String,
      status: String
  ): IO[User] =
    collection
      .find(and(equal("username", username), equal("status", status)))
      .head()
      .map(e => if (e == null) throw new Exception(ObjectNotFound) else e)

  override def findByEmail(email: String): IO[User] =
    collection
      .find(equal("email", email))
      .head()
      .map(e => if (e == null) throw new Exception(ObjectNotFound) else e)

  override def updateByUsername(
      username: String,
      firstName: String,
      secondName: String,
      phoneNumber: String
  ): IO[UpdateResult] =
    collection
      .updateOne(
        equal("username", username),
        and(
          set("firstName", firstName),
          set("secondName", secondName),
          set("phoneNumber", phoneNumber)
        )
      )
      .toFuture()

  override def updateByUsername(
      username: String,
      password: String
  ): IO[UpdateResult] =
    collection
      .updateOne(equal("username", username), set("password", password))
      .toFuture()

  override def deleteByUsername(username: String): IO[UpdateResult] =
    collection
      .updateOne(equal("username", username), set("status", "INACTIVE"))
      .toFuture()
}
