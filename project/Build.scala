import sbt._
import Keys._
import play._
//import play.PlayImport._
import play.Play.autoImport._
import PlayKeys._
import sbtassembly.Plugin._
import AssemblyKeys._

object ApplicationBuild extends Build {
  val branch = "git rev-parse --abbrev-ref HEAD".!!.trim
  val commit = "git rev-parse --short HEAD".!!.trim
  val author = s"git show --format=%an -s $commit".!!.trim
  val buildDate = (new java.text.SimpleDateFormat("yyyyMMdd"))
    .format(new java.util.Date())
  val appVersion = "%s-%s-%s".format(branch, buildDate, commit)

  val commonSettings = Seq(
    version := appVersion,    
    scalaVersion := "2.10.5",
//    crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5",
//      "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4",
//      "2.11.5", "2.11.6", "2.11.7"),
    resolvers += Resolver.typesafeRepo("releases"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    resolvers += Resolver.url("Edulify Repository",
        url("https://edulify.github.io/modules/releases/"))(Resolver.ivyStylePatterns)
  )
  
  val commonDependencies = Seq(
    javaWs,
    javaJdbc,
    javaEbean,
    cache,
    filters,
    "com.zaxxer" % "HikariCP-java6" % "2.3.8"
      ,"com.edulify" %% "play-hikaricp" % "2.0.6"
      ,"mysql" % "mysql-connector-java" % "5.1.31"
      ,"org.postgresql" % "postgresql" % "9.4-1201-jdbc41"     
      ,"com.hazelcast" % "hazelcast" % "3.5.2" 
      ,"org.julienrf" %% "play-jsonp-filter" % "1.2"
      ,"commons-codec" % "commons-codec" % "1.9"
      ,"org.apache.lucene" % "lucene-core" % "4.10.0"
      ,"org.apache.lucene" % "lucene-analyzers-common" % "4.10.0"
      ,"org.apache.lucene" % "lucene-misc" % "4.10.0"
      ,"org.apache.lucene" % "lucene-highlighter" % "4.10.0"
      ,"org.apache.lucene" % "lucene-suggest" % "4.10.0"
      ,"org.apache.lucene" % "lucene-facet" % "4.10.0"
      ,"org.apache.lucene" % "lucene-queryparser" % "4.10.0"
      ,"com.github.fge" % "json-patch" % "1.9"
      ,"org.quartz-scheduler" % "quartz" % "2.2.1"
      ,"org.webjars" %% "webjars-play" % "2.3.0"
      ,"org.webjars" % "bootstrap" % "3.3.5"
      ,"org.webjars" % "typeaheadjs" % "0.10.5-1"
      ,"org.webjars" % "handlebars" % "2.0.0-1"
      ,"org.webjars" % "jquery-ui" % "1.11.2"
      ,"org.webjars" % "jquery-ui-themes" % "1.11.2"
      ,"org.webjars" % "angularjs" % "1.4.7"
      ,"org.webjars" % "angular-ui-bootstrap" % "0.14.3"
      ,"org.webjars" % "font-awesome" % "4.5.0"
      ,"org.webjars" % "html5shiv" % "3.7.2"
      ,"org.webjars" % "requirejs" % "2.1.15"
      ,"org.webjars" % "respond" % "1.4.2"
      ,"org.webjars" % "html2canvas" % "0.4.1"
      ,"org.reflections" % "reflections" % "0.9.8" notTransitive ()
      ,"colt" % "colt" % "1.2.0"
      //,"net.sf.jni-inchi" % "jni-inchi" % "0.8"
      ,"org.freehep" % "freehep-graphicsbase" % "2.4"
      ,"org.freehep" % "freehep-vectorgraphics" % "2.4"
      ,"org.freehep" % "freehep-graphicsio" % "2.4"
      ,"org.freehep" % "freehep-graphicsio-svg" % "2.4"
      ,"org.freehep" % "freehep-graphics2d" % "2.4"
      //,"ws.securesocial" %% "securesocial" % "master-SNAPSHOT"
      ,"org.webjars.bower" % "spin.js" % "2.0.2"
      ,"be.objectify" %% "deadbolt-java" % "2.3.3"
      , "net.sourceforge.htmlunit" % "htmlunit" % "2.14" % "test"
  )

  val scalaBuildOptions = Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:reflectiveCalls",
    "-language:implicitConversions",
    "-language:postfixOps",
    "-language:dynamics",
    "-language:higherKinds",
    "-language:existentials",
    "-language:experimental.macros"
  )

  val javaBuildOptions = Seq(
    "-encoding", "UTF-8"
      //,"-Xlint:-options"
      //,"-Xlint:deprecation"
  )

  val build = Project("build", file("modules/build"))
    .settings(commonSettings:_*).settings(
    sourceGenerators in Compile <+= sourceManaged in Compile map { dir =>
      val file = dir / "BuildInfo.java"
      IO.write(file, """
package ix;
public class BuildInfo { 
   public static final String BRANCH = "%s";
   public static final String DATE = "%s";
   public static final String COMMIT = "%s";
   public static final String TIME = "%s";
   public static final String AUTHOR = "%s";
}
""".format(branch, buildDate, commit, new java.util.Date(), author))
      Seq(file)
    }
  )

  val seqaln = Project("seqaln", file("modules/seqaln"))
    .settings(commonSettings:_*).settings(
    libraryDependencies ++= commonDependencies,
    javacOptions ++= javaBuildOptions,
    mainClass in (Compile,run) := Some("ix.seqaln.SequenceIndexer")
  )

  val core = Project("core", file("."))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
    libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
  ).dependsOn(build,seqaln).aggregate(build,seqaln)

  val ncats = Project("ncats", file("modules/ncats"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
        //javaOptions in Runtime += "-Dconfig.resource=ncats.conf"
  ).dependsOn(core).aggregate(core)

  val marvin = Project("marvin", file("modules/marvin"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
    libraryDependencies ++= commonDependencies
  ).dependsOn(ncats).aggregate(ncats)
  
  // needs to specify on the commandline during development and dist
  //  sbt -Dconfig.file=modules/granite/conf/granite.conf granite/run
  val granite = Project("granite", file("modules/granite"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
      //javaOptions in Runtime += "-Dconfig.resource=granite.conf"
  ).dependsOn(ncats).aggregate(ncats)

  val idg = Project("idg", file("modules/idg"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
    libraryDependencies ++= commonDependencies,
      libraryDependencies += "org.webjars" % "morrisjs" % "0.5.1",
      libraryDependencies += "org.webjars" % "fabric.js" % "1.4.12",
      javacOptions ++= javaBuildOptions
      //javaOptions in Runtime += "-Dconfig.resource=pharos.conf"
  ).dependsOn(marvin).aggregate(marvin)

  val ginas = Project("ginas", file("modules/ginas"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      libraryDependencies += "org.webjars" % "dojo" % "1.10.0",
      libraryDependencies += "org.webjars" % "momentjs" % "2.11.0",
      libraryDependencies += "org.webjars" % "lodash" % "4.0.0",
      libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % Test,
      libraryDependencies  += "junit" % "junit" % "4.11" % Test,
      libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % Test,
      javacOptions ++= javaBuildOptions,
      javaOptions in Test += "-Dconfig.file=conf/ginas.conf",
    cleanFiles += file("modules/ginas/ginas.ix")
  ).dependsOn(ncats).aggregate(ncats)

  val hcs = Project("hcs", file("modules/hcs"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
  ).dependsOn(ncats).aggregate(ncats)

  val srs = Project("srs", file("modules/srs"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
  ).dependsOn(ncats).aggregate(ncats)

  val reach = Project("reach", file("modules/reach"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      libraryDependencies +="org.webjars" % "highcharts" % "4.0.4",
      libraryDependencies +="org.webjars" % "highslide" % "4.1.13",
      javacOptions ++= javaBuildOptions
  ).dependsOn(ncats).aggregate(ncats)

  val qhts = Project("qhts", file("modules/qhts"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
  ).dependsOn(marvin).aggregate(marvin)

  val tox21 = Project("tox21", file("modules/tox21"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
  ).dependsOn(qhts).aggregate(qhts)

  val ntd = Project("ntd", file("modules/ntd"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
      //javaOptions in Runtime += "-Dconfig.resource=pharos.conf"
    ).dependsOn(ncats).aggregate(ncats)

  val cbc = Project("cbc", file("modules/cbc"))
    .enablePlugins(PlayJava).settings(commonSettings:_*).settings(
      libraryDependencies ++= commonDependencies,
      javacOptions ++= javaBuildOptions
  ).dependsOn(ncats).aggregate(ncats)

  val ginasEvo = Project("ginas-evolution", file("modules/ginas-evolution"))
    .settings(assemblySettings: _*)
    .settings(
    libraryDependencies += "org.avaje.ebeanorm" % "avaje-ebeanorm" % "3.3.4",
      libraryDependencies += "com.typesafe" % "config" % "1.2.0",
      libraryDependencies += "com.jolbox" % "bonecp" % "0.8.0.RELEASE",
      libraryDependencies += "org.reflections" % "reflections" % "0.9.8" notTransitive (),
      unmanagedResources in Compile +=  file("conf/sql/post/ginas-oracle.sql"),
      unmanagedResources in Compile +=  file("modules/ginas/conf/evolutions/default/1.sql"),
      unmanagedJars in Compile += file("lib/ojdbc6.jar"),
      //unmanagedSourceDirectories in Compile += file("app/ix/ginas"),
      mainClass in (Compile,run) := Some("ix.ginas.utils.Evolution")
  )
}
