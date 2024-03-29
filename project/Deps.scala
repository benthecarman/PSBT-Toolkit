import sbt._

object Deps {

  lazy val osName: String = System.getProperty("os.name") match {
    case n if n.startsWith("Linux")   => "linux"
    case n if n.startsWith("Mac")     => "mac"
    case n if n.startsWith("Windows") => "win"
    case _                            => throw new Exception("Unknown platform!")
  }

  object V {
    val akkaV = "10.2.7"
    val akkaStreamV = "2.6.18"

    val scalaFxV = "17.0.1-R26"
    val bitcoinsV = "1.8.0-87-3cd57d37-SNAPSHOT"
    val javaFxV = "14.0.2.1"
  }

  object Compile {

    val akkaHttp =
      "com.typesafe.akka" %% "akka-http" % V.akkaV withSources () withJavadoc ()

    val akkaStream =
      "com.typesafe.akka" %% "akka-stream" % V.akkaStreamV withSources () withJavadoc ()

    val akkaActor =
      "com.typesafe.akka" %% "akka-actor" % V.akkaStreamV withSources () withJavadoc ()

    val bitcoinsBitcoindRpc =
      "org.bitcoin-s" %% "bitcoin-s-bitcoind-rpc" % V.bitcoinsV withSources () withJavadoc ()

    val bitcoinsTestKit =
      "org.bitcoin-s" %% "bitcoin-s-testkit-core" % V.bitcoinsV withSources () withJavadoc ()

    val bitcoinsCommons =
      "org.bitcoin-s" %% "bitcoin-s-app-commons" % V.bitcoinsV withSources () withJavadoc ()

    val scalaFx =
      "org.scalafx" %% "scalafx" % Deps.V.scalaFxV withSources () withJavadoc ()

    lazy val javaFxBase =
      "org.openjfx" % s"javafx-base" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxControls =
      "org.openjfx" % s"javafx-controls" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxFxml =
      "org.openjfx" % s"javafx-fxml" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxGraphics =
      "org.openjfx" % s"javafx-graphics" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxMedia =
      "org.openjfx" % s"javafx-media" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxSwing =
      "org.openjfx" % s"javafx-swing" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxWeb =
      "org.openjfx" % s"javafx-web" % V.javaFxV classifier osName withSources () withJavadoc ()

    lazy val javaFxDeps = List(javaFxBase,
                               javaFxControls,
                               javaFxFxml,
                               javaFxGraphics,
                               javaFxMedia,
                               javaFxSwing,
                               javaFxWeb)
  }

  val gui: List[ModuleID] = List(
    Compile.akkaActor,
    Compile.akkaHttp,
    Compile.akkaStream,
    Compile.bitcoinsBitcoindRpc,
    Compile.bitcoinsTestKit,
    Compile.bitcoinsCommons,
    Compile.scalaFx
  ) ++ Compile.javaFxDeps
}
