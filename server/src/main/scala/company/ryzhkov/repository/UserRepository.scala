package company.ryzhkov.repository

import cats.effect.IO
import company.ryzhkov.model.{User, UserFilter}
import org.mongodb.scala.Completed

trait UserRepository {
  def save(user: User): IO[Completed]
  def findOne(criteria: UserFilter): IO[Option[User]]
}
