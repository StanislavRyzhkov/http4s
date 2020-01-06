package company.ryzhkov.model

import java.util.Date

import org.bson.types.ObjectId

object Text {
  def apply(
      created: Date,
      title: String,
      englishTitle: String,
      kind: String,
      textComponents: Seq[TextComponent],
      replies: Seq[Reply]
  ): Text =
    Text(
      new ObjectId(),
      created,
      title,
      englishTitle,
      kind,
      textComponents,
      replies
    )
}

case class TextComponent(
    number: Int,
    tag: String,
    content: String,
    source: String
)

case class Reply(username: String, content: String, created: Date)

case class TextInfo(
    id: String,
    mainImage: String,
    firstParagraph: String,
    title: String,
    englishTitle: String,
    date: String
)

case class TextFull(
    id: String,
    date: String,
    title: String,
    englishTitle: String,
    textComponents: Seq[TextComponent],
    replies: Seq[ReplyStringDate]
)

case class ReplyStringDate(username: String, content: String, created: String)

case class CreateReply(englishTitle: String, content: String)

case class Text(
    _id: ObjectId,
    created: Date,
    title: String,
    englishTitle: String,
    kind: String,
    textComponents: Seq[TextComponent],
    replies: Seq[Reply]
)

sealed trait TextFilter

case class EnglishTitle(englishTitle: String) extends TextFilter

case class Kind(kind: String) extends TextFilter

sealed trait TextSorting

case object Created extends TextSorting
