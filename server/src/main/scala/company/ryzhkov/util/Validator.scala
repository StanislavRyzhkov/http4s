package company.ryzhkov.util

case class Validator[A](obj: A) {
  def check(f: A => Boolean, message: String): Validator[A] = {
    if (f(obj)) this
    else throw new Exception(message)
  }

  def create(): A = this.obj
}
