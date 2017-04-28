enablePlugins(JavaAppPackaging)

val akkaV = "2.5.0"
val akkaHttpV = "10.0.5"

lazy val root = (project in file("."))
  .settings(
    name := "TicketReservation",
    scalaVersion := "2.12.1",
    scalacOptions ++= Seq (
      "-deprecation",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:reflectiveCalls",
      "-language:implicitConversions",
      "-language:postfixOps"
    ),
  libraryDependencies ++= Seq(
    "com.github.melrief" %% "pureconfig" % "0.4.0",
    "com.typesafe.akka"  %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka"  %% "akka-http" % akkaHttpV,
    "com.typesafe.akka"  %% "akka-http-spray-json" % akkaHttpV
  )
)
