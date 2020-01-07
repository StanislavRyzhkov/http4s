package company.ryzhkov.model

import company.ryzhkov.util.Validator
import company.ryzhkov.util.ValidatorImplicits._
import org.bson.types.ObjectId

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
  def validate: Register =
    Validator[Register](this)
      .check(_.username.validateMaxLength(100))(
        "Имя пользователя от 1 до 100 символов"
      )
      .check(_.username.validateMinLength(1))(
        "Имя пользователя от 1 до 100 символов"
      )
      .check(_.email.validateEmail)("Некорреткный email")
      .check(_.password1.validateMinLength(5))("Пароль не менее 5 символов")
      .check(_.password1.validateMaxLength(100))("Пароль не более 100 символов")
      .check(r => r.password1 == r.password2)("Пароли не совпадают")
      .create()
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
)

case class DeleteAccount(username: String, password1: String, password2: String)

case class UpdatePassword(
    oldPassword: String,
    newPassword1: String,
    newPassword2: String
)
