package company.ryzhkov.util

import cats.effect._
import org.http4s.Request

trait HeaderReceiver {
  def transform(req: Request[IO]): Option[String] =
    req.headers.find(_.name.toString() == "Authorization").map(_.value)
}
