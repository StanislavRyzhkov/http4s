package company.ryzhkov.util

import cats.effect.IO
import company.ryzhkov.util.Constants.ObjectNotFound
import org.mongodb.scala.{FindObservable, SingleObservable}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ApplicationImplicits {

  implicit def foo[A](future: Future[A]): IO[A] = IO.fromFuture(IO(future))

//  implicit def findObservable2IOOption[A](
//      fo: FindObservable[A]
//  ): IO[A] =
//    IO.fromFuture(
//      IO(
//        fo.head()
//          .map(
//            e =>
//              if (e == null) throw new Exception(ObjectNotFound)
//              else e
//          )
//      )
//    )
//
//  implicit def findObservable2IO[A](fo: FindObservable[A]): IO[Seq[A]] =
//    IO.fromFuture(IO(fo.toFuture()))
//
//  implicit def singleObservable2IO[A](so: SingleObservable[A]): IO[A] =
//    IO.fromFuture(IO(so.toFuture()))
}
