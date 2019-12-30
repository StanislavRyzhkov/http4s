package company.ryzhkov

import cats.effect.{ContextShift, IO}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Main2 extends App {
  implicit val ctx: ContextShift[IO] = IO.contextShift(global)

//  val f = Future {
//    Thread.sleep(3000)
//
//    println(5)
//  }

  val io = IO.fromFuture(IO(Future {


    Thread.sleep(3000)
    throw new Exception("OOPS")

    println(3)
  }))

  io.unsafeRunAsync{
    case Right(_) => ()
    case Left(value) => println(value.getMessage)
  }
  println("END")

  Thread.sleep(10000)
}
