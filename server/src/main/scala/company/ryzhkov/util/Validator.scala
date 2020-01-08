package company.ryzhkov.util

import java.util.regex.Pattern

import cats.effect.IO
import company.ryzhkov.exception.ValidationException

case class Validator[A](obj: A) {
  def check(f: A => Boolean)(message: String): Validator[A] = {
    if (f(obj)) this
    else throw ValidationException(message)
  }

  def create(): IO[A] = IO(this.obj)
}

case class ExtendedString(string: String) {
  def validateMaxLength(max: Int): Boolean =
    string.length < max

  def validateMinLength(min: Int): Boolean =
    string.length >= min

  def validateEmail: Boolean =
    Pattern
      .compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
      .matcher(string)
      .matches()
}

object ValidatorImplicits {
  implicit def string2ExtendedString(string: String): ExtendedString =
    ExtendedString(string)
}
