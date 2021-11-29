name := """redditlyzer"""
organization := "com.example"

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
lazy val akkaVersion = "2.6.14"
lazy val akkaHttpVersion = "10.1.14"

scalaVersion := "2.13.6"

libraryDependencies += guice
libraryDependencies ++= Seq(javaWs)
libraryDependencies += ehcache

// Akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http-jackson" % akkaHttpVersion
libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
