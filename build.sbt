enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

maintainer in Docker := "Alexey Ponkin <alexey.ponkin@gmail.com>"

packageSummary in Docker := "Ticket reservation system"

dockerExposedPorts ++= Seq(8080)

daemonUser := "tss"

dockerRepository := Some("ponkin")

mappings in Universal += {
  // we are using the reference.conf as default application.conf
  // the user can override settings here
  val conf = (resourceDirectory in Compile).value / "reference.conf"
  conf -> "conf/application.conf"
}

javaOptions in Universal ++= Seq(
    // -J params will be added as jvm parameters
    "-J-Xmx256mb",
    "-J-Xms1g"
)

bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/application.conf""""

val akkaV = "2.5.0"
val akkaHttpV = "10.0.5"

lazy val root = (project in file("."))
  .settings(
    name := "TicketReservation",
    scalaVersion := "2.12.1",
    scalacOptions ++= Seq (
      "-deprecation",
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"
    ),
  libraryDependencies ++= Seq(
    "com.github.melrief" %% "pureconfig" % "0.4.0",
    "com.typesafe.akka"  %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka"  %% "akka-http" % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
)
