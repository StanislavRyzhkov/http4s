package company.ryzhkov.model

import cats.effect.IO
import company.ryzhkov.util.Validator
import company.ryzhkov.util.ValidatorImplicits._
import org.bson.types.ObjectId

object Constants {
  val Username_1_100         = "Имя пользователя от 1 до 100 символов"
  val InvalidEmail           = "Некорректный email"
  val Password_5             = "Пароль не менее 5 символов"
  val Password_100           = "Пароль не более 100 символов"
  val Passwords_Do_Not_Match = "Пароли не совпадают"
}

object User {
  def apply(username: String, email: String, password: String): User =
    User(new ObjectId(), username, email, password)
}

case class User(
    _id: ObjectId,
    username: String,
    email: String,
    password: String,
    firstName: String = "",
    secondName: String = "",
    phoneNumber: String = "",
    status: String = "ACTIVE",
    roles: Seq[String] = List("ROLE_USER")
)

case class Auth(username: String, password: String)

case class Register(
    username: String,
    email: String,
    password1: String,
    password2: String
) {
  def validate: IO[Register] = {
    import Constants._

    Validator[Register](this)
      .check(_.username.validateMaxLength(max = 100))(Username_1_100)
      .check(_.username.validateMinLength(min = 1))(Username_1_100)
      .check(_.email.validateEmail)(InvalidEmail)
      .check(_.password1.validateMinLength(min = 5))(Password_5)
      .check(_.password1.validateMaxLength(max = 100))(Password_100)
      .check(r => r.password1 == r.password2)(Passwords_Do_Not_Match)
      .create()
  }
}

case class Account(
    username: String,
    email: String,
    firstName: String,
    secondName: String,
    phoneNumber: String
)

case class UpdateAccount(
    firstName: String,
    secondName: String,
    phoneNumber: String
) {
  def validate: IO[UpdateAccount] =
    Validator(this)
      .check(_.firstName.validateMaxLength(3))("OOPS!")
      .create()
}

case class DeleteAccount(
    username: String,
    password1: String,
    password2: String
) {
  def validate: IO[DeleteAccount] = {
    import Constants._

    Validator(this)
      .check(da => da.password1 == da.password2)(Passwords_Do_Not_Match)
      .create()
  }
}

case class UpdatePassword(
    oldPassword: String,
    newPassword1: String,
    newPassword2: String
)
