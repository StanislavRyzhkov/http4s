package company.ryzhkov.model

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
)

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
