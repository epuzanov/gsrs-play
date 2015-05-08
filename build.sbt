name := "reach"

version := "1.0.0"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

//lazy val admin = (project in file("modules/admin")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaWs,
  javaJdbc,
  javaEbean,
  cache,
  "com.typesafe.akka" % "akka-cluster_2.11" % "2.3.4"
    //,"com.typesafe.akka" % "akka-docs_2.11" % "2.3.9"
    ,"mysql" % "mysql-connector-java" % "5.1.31"
    ,"commons-codec" % "commons-codec" % "1.3"
    ,"org.apache.lucene" % "lucene-core" % "4.10.0"
    ,"org.apache.lucene" % "lucene-analyzers-common" % "4.10.0"
    ,"org.apache.lucene" % "lucene-misc" % "4.10.0"
    ,"org.apache.lucene" % "lucene-highlighter" % "4.10.0"
    ,"org.apache.lucene" % "lucene-suggest" % "4.10.0"
    ,"org.apache.lucene" % "lucene-facet" % "4.10.0"
    ,"org.apache.lucene" % "lucene-queryparser" % "4.10.0"
    ,"org.webjars" %% "webjars-play" % "2.3.0"
    ,"org.webjars" % "bootstrap" % "3.3.4"
    ,"org.webjars" % "typeaheadjs" % "0.10.5-1"
    ,"org.webjars" % "handlebars" % "2.0.0-1"
    ,"org.webjars" % "jquery-ui" % "1.11.2"
    ,"org.webjars" % "jquery-ui-themes" % "1.11.2"
    ,"org.webjars" % "angular-ui-bootstrap" % "0.11.0-2"
    //,"org.webjars" % "metroui" % "2.0.23"
    ,"org.webjars" % "font-awesome" % "4.2.0"
    ,"org.webjars" % "html5shiv" % "3.7.2"
    ,"org.webjars" % "requirejs" % "2.1.15"
    ,"org.webjars" % "respond" % "1.4.2"
    ,"org.webjars" % "highcharts" % "4.0.4"
    ,"org.webjars" % "highslide" % "4.1.13"
        ,"org.webjars" % "html2canvas" % "0.4.1"
    ,"org.reflections" % "reflections" % "0.9.8" notTransitive ()
    ,"colt" % "colt" % "1.2.0"
    ,"org.webjars" % "dojo" % "1.10.0"
    //,"net.sf.jni-inchi" % "jni-inchi" % "0.8"
    ,"org.freehep" % "freehep-graphicsbase" % "2.4"
    ,"org.freehep" % "freehep-vectorgraphics" % "2.4"
    ,"org.freehep" % "freehep-graphicsio" % "2.4"
    ,"org.freehep" % "freehep-graphicsio-svg" % "2.4"
    ,"org.freehep" % "freehep-graphics2d" % "2.4"
    ,"ws.securesocial" %% "securesocial" % "master-SNAPSHOT"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.7",
  "-encoding", "UTF-8"
)

javacOptions ++= Seq(
  "-source", "1.7",
  "-target", "1.7",
  "-encoding",
  "UTF-8",  "-Xlint:-options"
)

resolvers += Resolver.sonatypeRepo("snapshots")
