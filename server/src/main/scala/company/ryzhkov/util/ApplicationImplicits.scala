package company.ryzhkov.util

import cats.effect.IO

import scala.concurrent.Future

object ApplicationImplicits {
  implicit def foo[A](future: Future[A]): IO[A] = IO.fromFuture(IO(future))
}
