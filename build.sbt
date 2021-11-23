name := "psbt-toolkit"

version := "0.1.2"

scalaVersion := "2.13.3"

libraryDependencies ++= Deps.gui

run / fork := true

resolvers += Resolver.sonatypeRepo("snapshots")

enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", _ @_*) => MergeStrategy.discard
  case _                           => MergeStrategy.first
}
