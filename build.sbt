
name := "schema-registry-with-scala"

version := "0.2"
scalaVersion := "2.12.4"

description := """Trucking IoT on HDF"""
organization := "com.orendainx.trucking"
homepage := Some(url("https://github.com/orendain/schema-registry-with-scala"))
organizationHomepage := Some(url("https://github.com/orendain/schema-registry-with-scala"))
licenses := Seq(("Apache License 2.0", url("https://www.apache.org/licenses/LICENSE-2.0")))

resolvers += "Hortonworks Nexus" at "http://repo.hortonworks.com/content/repositories/releases"

libraryDependencies ++= Seq(
  "com.hortonworks.registries" % "schema-registry-serdes" % "0.3.0.3.0.1.1-5",
  "javax.xml.bind" % "jaxb-api" % "2.3.0",

  "com.typesafe" % "config" % "1.3.1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
)
