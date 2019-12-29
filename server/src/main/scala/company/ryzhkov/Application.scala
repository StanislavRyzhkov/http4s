package company.ryzhkov

import cats.effect._
import org.http4s._
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends App {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)

  case class Tweet(id: Int, message: String)

  implicit def tweetEncoder: EntityEncoder[IO, Tweet] = ???
  // tweetEncoder: org.http4s.EntityEncoder[cats.effect.IO,Tweet]

  implicit def tweetsEncoder: EntityEncoder[IO, Seq[Tweet]] = ???
  // tweetsEncoder: org.http4s.EntityEncoder[cats.effect.IO,Seq[Tweet]]

  val tweet = Tweet(1, "YES")

  def getTweet(tweetId: Int): IO[Tweet] = IO(tweet)
  // getTweet: (tweetId: Int)cats.effect.IO[Tweet]

  def getPopularTweets: IO[Seq[Tweet]] = IO(List(tweet))
  // getPopularTweets: ()cats.effect.IO[Seq[Tweet]]

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }

  val tweetService = HttpRoutes.of[IO] {
    case GET -> Root / "tweets" / "popular" =>
      getPopularTweets
        .flatMap(Ok(_))
    case GET -> Root / "tweets" / IntVar(tweetId) =>
      getTweet(tweetId)
        .flatMap(Ok(_))
  }
}
