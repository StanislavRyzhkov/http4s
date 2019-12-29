package company.ryzhkov

import cats.effect.{ContextShift, IO}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Main extends App {
  implicit val ctx: ContextShift[IO] = IO.contextShift(global)

  val f = Future {
    Thread.sleep(3000)
    println(1)
  }

  val io = IO {
    Thread.sleep(3000)
    println(5)
  }

  val e = IO.fromFuture(IO(f))

  e.unsafeRunAsync {
    case Right(v) => println(v)
    case Left(l) => println(l)
  }

//  val g = io.start
//  g.unsafeRunSync()

  println("END")
  Thread.sleep(15000)
}
