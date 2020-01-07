package company.ryzhkov.service

import cats.effect.IO
import company.ryzhkov.model.User
import company.ryzhkov.repository.UserRepository
import company.ryzhkov.util.TokenProvider

class UserService(userRepository: UserRepository) {

  def findUserByHeader(optionHeader: Option[String]): IO[User] =
    TokenProvider
      .getUsername(optionHeader)
      .flatMap { username =>
        userRepository.findByUsernameAndStatus(username, "ACTIVE")
      }
}
