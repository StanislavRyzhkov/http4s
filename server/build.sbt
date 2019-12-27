name := "server"

version := "0.1"

scalaVersion := "2.12.10"

val http4sVersion = "0.18.23"

lazy val common = (project in file("."))
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(AssemblyPlugin)
  .settings(
    name := "Stockholder",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime
    ),
    scalacOptions ++= Seq(
      "-feature", "-language:implicitConversions",
      "-Ypartial-unification"
    ),
    mainClass in (Compile, run) := Some("company.ryzhkov.Application"),
    mainClass in (assembly) := Some("company.ryzhkov.Application"),
    assemblyJarName in assembly := "server.jar"
  )
