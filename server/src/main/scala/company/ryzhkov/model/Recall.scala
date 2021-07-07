package company.ryzhkov.model

import java.util.Date

import org.bson.types.ObjectId

object Recall {
  def apply(
      author: String,
      email: String,
      topic: String,
      text: String
  ): Recall = {
    Recall(new ObjectId, author, email, topic, text)
  }
}

case class CreateRecall(
    author: String,
    email: String,
    topic: String,
    text: String
)

case class Recall(
    _id: ObjectId,
    author: String,
    email: String,
    topic: String,
    text: String,
    created: Date = new Date()
)
