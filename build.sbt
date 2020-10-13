name := "konekcijabaza"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.6"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.10"


libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"