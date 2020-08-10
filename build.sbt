name := "psbt-toolkit"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Deps.gui

resolvers += Resolver.sonatypeRepo("snapshots")
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.kenglxn.qrgen" % "javase" % "2.6.0"

enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
