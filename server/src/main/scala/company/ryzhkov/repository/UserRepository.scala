package company.ryzhkov.repository

import cats.effect.IO
import company.ryzhkov.model.User
import org.mongodb.scala.Completed
import org.mongodb.scala.result.UpdateResult

trait UserRepository {
  def save(user: User): IO[Completed]
  def findByUsername(username: String): IO[User]
  def findByUsernameAndStatus(username: String, status: String): IO[User]
  def findByEmail(email: String): IO[User]
  def updateByUsername(
      username: String,
      firstName: String,
      secondName: String,
      phoneNumber: String
  ): IO[UpdateResult]
  def updateByUsername(username: String, password: String): IO[UpdateResult]
  def deleteByUsername(username: String): IO[UpdateResult]
}
