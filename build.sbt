name := """bitcoinj"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.bitcoinj" % "bitcoinj-core" % "0.14.7",
  "org.bitcoinj" % "bitcoinj-tools" % "0.14.7",
  "org.bitcoinj" % "bitcoinj-parent" % "0.14.7",
  "org.bitcoinj" % "bitcoinj-examples" % "0.14.7",
  "org.bitcoinj" % "wallettemplate" % "0.14.7",
  "org.bitcoinj" % "orchid" % "1.2.1",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.25",
  "com.typesafe.play" %% "play-native-loader" % "1.0.0",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
unmanagedBase := baseDirectory.value / "lib"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

