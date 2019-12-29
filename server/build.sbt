name := "server"

version := "0.1"

scalaVersion := "2.12.10"

val http4sVersion = "0.20.15"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val common = (project in file("."))
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(AssemblyPlugin)
  .settings(
    name := "Stockholder",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % "0.11.2",
      "io.circe" %% "circe-literal" % "0.11.2",
      "org.mongodb.scala" %% "mongo-scala-driver" % "2.7.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
    ),
    scalacOptions ++= Seq(
      "-Ypartial-unification",
      "-feature", "-language:implicitConversions"
    ),
    mainClass in (Compile, run) := Some("company.ryzhkov.Application"),
    mainClass in (assembly) := Some("company.ryzhkov.Application"),
    assemblyJarName in assembly := "server.jar"
  )
