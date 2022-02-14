ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

libraryDependencies += "com.slack.api" % "bolt" % "1.18.0"
libraryDependencies += "com.slack.api" % "bolt-socket-mode" % "1.18.0"
libraryDependencies += "javax.websocket" % "javax.websocket-api" % "1.1"
libraryDependencies += "org.glassfish.tyrus.bundles" % "tyrus-standalone-client" % "1.17"
libraryDependencies += "com.nulab-inc" % "backlog4j" % "2.4.4"
libraryDependencies += "org.scalatest" %% "scalatest-funsuite" % "3.2.11" % "test"

lazy val root = (project in file("."))
  .settings(
    name := "slack-scala"
  )
