package company.ryzhkov.service

import cats.effect.IO
import company.ryzhkov.model.{Account, Auth, Register, UpdateAccount, User}
import company.ryzhkov.repository.UserRepository
import company.ryzhkov.util.Constants._
import company.ryzhkov.util.{PasswordEncoder, TokenProvider}
import org.mongodb.scala.{Completed, result}

class UserService(userRepository: UserRepository) extends PasswordEncoder {

  def register(register: Register): IO[Completed] = {
    val Register(username, email, password, _) = register
    val tuple = for {
      usernameUnique <- checkUsernameUnique(username)
      emailUnique    <- checkEmailUnique(email)
    } yield (usernameUnique, emailUnique)
    tuple flatMap {
      case (false, _) => IO.raiseError(new Exception(UsernameAlreadyExists))
      case (_, false) => IO.raiseError(new Exception(EmailAlreadyExists))
      case _          => userRepository.save(User(username, email, encode(password)))
    }
  }

  def authenticate(auth: Auth): IO[String] =
    userRepository
      .findByUsernameAndStatus(auth.username, "ACTIVE")
      .handleErrorWith(
        _ => IO.raiseError(new Exception(InvalidUsernameOrPassword))
      )
      .flatMap { user =>
        if (decode(auth.password, user.password))
          TokenProvider.createToken(user.username)
        else IO.raiseError(new Exception(InvalidUsernameOrPassword))
      }

  def findUserByHeader(optionHeader: Option[String]): IO[User] =
    TokenProvider
      .getUsername(optionHeader)
      .flatMap { username =>
        userRepository.findByUsernameAndStatus(username, "ACTIVE")
      }

  def findUsernameByHeader(optionHeader: Option[String]): IO[String] =
    findUserByHeader(optionHeader).map(_.username)

  def findAccountByHeader(optionHeader: Option[String]): IO[Account] =
    findUserByHeader(optionHeader).map(userToAccount)

  def updateAccount(
      optionalHeader: Option[String],
      updateAccount: UpdateAccount
  ): IO[result.UpdateResult] =
    for {
      username <- findUsernameByHeader(optionalHeader)
      updateResult <- userRepository.updateByUsername(
                       username,
                       updateAccount.firstName,
                       updateAccount.secondName,
                       updateAccount.phoneNumber
                     )
    } yield updateResult

  private def checkUsernameUnique(username: String): IO[Boolean] =
    userRepository
      .findByUsername(username)
      .map(_ => false)
      .handleErrorWith(_ => IO(true))

  private def checkEmailUnique(email: String): IO[Boolean] =
    userRepository
      .findByEmail(email)
      .map(_ => false)
      .handleErrorWith(_ => IO(true))

  private def userToAccount(user: User): Account =
    Account(
      user.username,
      user.email,
      user.firstName,
      user.secondName,
      user.phoneNumber
    )
}
