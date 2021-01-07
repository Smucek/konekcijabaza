name := "konekcijabaza"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.6"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.12"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.6.10"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % "2.6.10"
//libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"



libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
    "org.slf4j" % "slf4j-api" % "1.7.1",
    "ch.qos.logback" % "logback-core" % "1.0.7",
    "ch.qos.logback" % "logback-classic" % "1.0.7"
)

libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"

libraryDependencies += "com.typesafe" % "config" % "1.2.1"

libraryDependencies += "com.pauldijou" %% "jwt-spray-json" % "4.2.0"