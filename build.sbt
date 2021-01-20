name := "producer-app"

version := "1.0"

scalaVersion := "2.13.3"

enablePlugins(JavaAppPackaging, DockerComposePlugin)

dockerImageCreationTask := (publishLocal in Docker).value

dockerExposedPorts := Seq(8080)
resolvers ++= Seq (
  "Confluent" at "http://packages.confluent.io/maven"
)
libraryDependencies ++= Vector(
  "org.apache.avro" % "avro" % "1.10.0",
  "org.apache.kafka" % "kafka-clients" % "2.4.0",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "slf4j-simple" % "1.7.25",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "io.confluent" % "kafka-avro-serializer" % "5.5.0")