import scalariform.formatter.preferences._

name          := "ra-scraper"
organization  := "su.muride"
version       := "0.0.1"
scalaVersion  := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

fork := true

libraryDependencies ++= {
  val scalazV          = "7.1.5"
  val akkaStreamV      = "2.0.4"
  val scalaTestV       = "3.0.0-M10"
  val scalaMockV       = "3.2.2"
  val scalazScalaTestV = "0.3.0"
  val slickV           = "3.1.1"
  val flywayV = "4.0"
  val h2V = "1.4.190"

  Seq(
    "org.scalaz"         %% "scalaz-core"                          % scalazV,
    "com.typesafe.akka"  %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka"  %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka"  %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.slick" %% "slick"                                % slickV,
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
    "joda-time" % "joda-time" % "2.7",
    "org.joda" % "joda-convert" % "1.7",
    "org.postgresql"     %  "postgresql"                           % "9.4-1205-jdbc41",
    "org.slf4j"          %  "slf4j-simple"                         % "1.7.13",
    "org.mindrot"        %  "jbcrypt"                              % "0.3m",
    "org.flywaydb"       %  "flyway-core"                          % flywayV,

    "com.google.guava"   %  "guava"                                % "18.0",
    "net.ruippeixotog"   %% "scala-scraper"                        % "0.1.2",
    "com.github.nscala-time" %% "nscala-time"                      % "2.6.0",

    "org.scalatest"      %% "scalatest"                            % scalaTestV       % "test",
    "org.scalamock"      %% "scalamock-scalatest-support"          % scalaMockV       % "test",
    "org.scalaz"         %% "scalaz-scalacheck-binding"            % scalazV          % "test",
    "org.typelevel"      %% "scalaz-scalatest"                     % scalazScalaTestV % "test",
    "com.typesafe.akka"  %% "akka-http-testkit-experimental"       % akkaStreamV      % "test"
  )
}

lazy val root = project.in(file(".")).configs()

scalariformSettings

Revolver.settings
enablePlugins(JavaAppPackaging)

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

initialCommands := """|import scalaz._
                      |import Scalaz._
                      |import akka.actor._
                      |import akka.pattern._
                      |import akka.util._
                      |import scala.concurrent._
                      |import scala.concurrent.duration._""".stripMargin

publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
pomExtra := (
  <url>http://yeghishe.github.io/</url>
  <licenses>
    <license>
      <name>Apache-2.0</name>
      <url>http://opensource.org/licenses/Apache-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/th0br0/</url>
    <connection>scm:git:git@github.com:th0br0/.git</connection>
  </scm>
  <developers>
    <developer>
      <id>th0br0</id>
      <name>Andreas C. Osowski</name>
      <url>http://muride.su/</url>
    </developer>
  </developers>)
