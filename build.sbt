name := """warp-engine"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"

libraryDependencies ++= Seq(
    cache,
    ws,
    "com.typesafe.play.plugins" %% "play-plugins-redis" % "2.3.1"
)
