package company.ryzhkov.util

import cats.effect.IO
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.{Completed, FindObservable, SingleObservable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ApplicationImplicits {
//  implicit def toIO[A](future: Future[A]): IO[A] = IO.fromFuture(IO(future))
//
//  implicit def findObservable2Option[A](
//      fo: FindObservable[A]
//  ): Future[Option[A]] =
//    fo.head().map(Option(_))

  implicit def findObservable2IOOption[A](fo: FindObservable[A]): IO[Option[A]] =
    IO.fromFuture(IO(fo.head().map(Option(_))))

  implicit def findObservable2IO[A](fo: FindObservable[A]): IO[Seq[A]] =
    IO.fromFuture(IO(fo.toFuture()))

//  implicit def singleObservable2IOCompleted(
//      so: SingleObservable[Completed]
//  ): IO[Completed] =
//    IO.fromFuture(IO(so.toFuture()))
//
//  implicit def singleObservable2IOUpdateResult(so: SingleObservable[UpdateResult]): IO[UpdateResult] =
//    IO.fromFuture(IO(so.toFuture()))

  implicit def singleObservable2IO[A](so: SingleObservable[A]): IO[A] =
    IO.fromFuture(IO(so.toFuture()))
}
