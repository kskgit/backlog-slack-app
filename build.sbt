import sbt.Compile

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

libraryDependencies += "com.slack.api" % "bolt" % "1.18.0"
libraryDependencies += "com.slack.api" % "bolt-socket-mode" % "1.18.0"
libraryDependencies += "javax.websocket" % "javax.websocket-api" % "1.1"
libraryDependencies += "org.glassfish.tyrus.bundles" % "tyrus-standalone-client" % "1.17"
libraryDependencies += "com.nulab-inc" % "backlog4j" % "2.4.4"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test
libraryDependencies += "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % "test"
libraryDependencies += "com.google.inject" % "guice" % "5.1.0"
libraryDependencies += "com.google.firebase" % "firebase-admin" % "8.1.0"
libraryDependencies += "com.slack.api" % "bolt-jetty" % "1.20.0"
Compile /
  herokuAppName := "lit-eyrie-67457"

lazy val root = (project in file("."))
  .settings(
    name := "slack-scala"
  )
  .enablePlugins(JavaServerAppPackaging)
