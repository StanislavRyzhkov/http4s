package company.ryzhkov.util

import at.favre.lib.crypto.bcrypt.BCrypt

trait PasswordEncoder {
  def encode(rawPassword: String): String =
    BCrypt.withDefaults().hashToString(8, rawPassword.toCharArray)

  def decode(rawPassword: String, passwordHash: String): Boolean = {
    val result = BCrypt.verifyer().verify(rawPassword.toCharArray, passwordHash)
    result.verified
  }
}
