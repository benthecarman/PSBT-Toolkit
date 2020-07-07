name := "psbt-toolkit"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= Deps.gui

enablePlugins(JavaAppPackaging, GraalVMNativeImagePlugin)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _ @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
