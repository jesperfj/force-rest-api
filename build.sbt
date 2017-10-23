name := "force-rest-api"
organization := "com.frejo"
version := "1.0.1"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.9",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.9.0",
  "com.fasterxml.jackson.datatype" % "jackson-datatype-joda" % "2.9.0"
)

publishTo := Some("ForceRestApi" at "s3://s3-us-east-1.amazonaws.com/docurated-build/force-rest-api")