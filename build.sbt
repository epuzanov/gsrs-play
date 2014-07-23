name := "crosstalk"

version := "0.0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.apache.lucene" % "lucene-core" % "4.9.0"
  ,"org.apache.lucene" % "lucene-analyzers-common" % "4.9.0"
  ,"org.apache.lucene" % "lucene-misc" % "4.9.0"
  ,"org.apache.lucene" % "lucene-highlighter" % "4.9.0"
  ,"org.apache.lucene" % "lucene-suggest" % "4.9.0"
  ,"org.apache.lucene" % "lucene-facet" % "4.9.0"
  ,"org.webjars" %% "webjars-play" % "2.3.0"
  ,"org.webjars" % "bootstrap" % "3.2.0"
  ,"org.webjars" % "angular-ui-bootstrap" % "0.11.0-2"
  ,"mysql" % "mysql-connector-java" % "5.1.31"
//  ,"com.fasterxml.jackson.core" % "jackson-core" % "2.4.1"
//  ,"com.fasterxml.jackson.core" % "jackson-annotations" % "2.4.1"
//  ,"com.fasterxml.jackson.core" % "jackson-databind" % "2.4.1"
)     

