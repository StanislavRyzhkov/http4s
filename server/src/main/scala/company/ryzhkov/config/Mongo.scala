package company.ryzhkov.config

import company.ryzhkov.model._
import org.bson.codecs.configuration.CodecRegistries._
import org.bson.codecs.configuration.CodecRegistry
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}

object Mongo {
  lazy val mongoClient: MongoClient = MongoClient("mongodb://localhost")
  lazy val customCodecUser: CodecRegistry = fromProviders(classOf[User])
  lazy val customCodecText: CodecRegistry =
    fromProviders(classOf[Text], classOf[TextComponent], classOf[Reply])
  lazy val customCodecRecall: CodecRegistry = fromProviders(classOf[Recall])

  lazy val codecRegistry: CodecRegistry =
    fromRegistries(
      customCodecUser,
      customCodecText,
      customCodecRecall,
      DEFAULT_CODEC_REGISTRY
    )

  lazy val database: MongoDatabase =
    mongoClient.getDatabase("fin_scala").withCodecRegistry(codecRegistry)

  lazy val userCollection: MongoCollection[User] =
    database.getCollection("users")

  lazy val textCollection: MongoCollection[Text] =
    database.getCollection("texts")

  lazy val recallCollection: MongoCollection[Recall] =
    database.getCollection("recalls")
}
