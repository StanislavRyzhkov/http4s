package company.ryzhkov

import java.lang.Throwable

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends App {
  implicit val ctx: ContextShift[IO] = IO.contextShift(global)

//  val f = Future {
//    Thread.sleep(3000)
//    println(1)
//  }

  val io = IO {
    Thread.sleep(3000)
    5
  }


  val k = for {
    _ <- Future(1)
    _ <- Future(2)
    n <- Future(3)
  } yield n

  Future(1)
    .flatMap(_ => Future(2))
    .flatMap(_ => Future(3))




//  val e = IO.fromFuture(IO(f))

//  e.unsafeRunAsync {
//    case Right(v) => println(v)
//    case Left(l) => println(l)
//  }

//  val g = io.start
  io.unsafeRunAsync {
    case Right(value) => println(value)
    case Left(value) => println(value)
  }
//  g.unsafeRunSync()

  println("END")
  Thread.sleep(15000)
}
