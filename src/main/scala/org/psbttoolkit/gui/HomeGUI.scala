package org.psbttoolkit.gui

import org.psbttoolkit.gui.generators.GeneratorsPane
import org.psbttoolkit.gui.psbts.PSBTsPane
import org.psbttoolkit.gui.transactions.TransactionsPane
import scalafx.application.JFXApp
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.scene.control._
import scalafx.scene.image.Image
import scalafx.scene.layout.{BorderPane, StackPane, VBox}

object HomeGUI extends JFXApp {
  // Catch unhandled exceptions on FX Application thread
  Thread
    .currentThread()
    .setUncaughtExceptionHandler((_: Thread, ex: Throwable) => {
      ex.printStackTrace()
      val _ = new Alert(AlertType.Error) {
        initOwner(owner)
        title = "Unhandled exception"
        headerText = "Exception: " + ex.getClass + ""
        contentText = Option(ex.getMessage).getOrElse("")
      }.showAndWait()
    })

  private val glassPane = new VBox {
    children = new ProgressIndicator {
      progress = ProgressIndicator.IndeterminateProgress
      visible = true
    }
    alignment = Pos.Center
    visible = false
  }

  private val statusLabel = new Label {
    maxWidth = Double.MaxValue
    padding = Insets(0, 10, 10, 10)
    text <== GlobalData.statusText
  }

  private val resultArea = new TextArea {
    editable = false
    wrapText = true
    text = "Welcome"
  }

  private val tabPane: TabPane = new TabPane() {

    val psbtsTab: Tab = new Tab {
      text = "PSBTs"
      content = new PSBTsPane().view
    }

    val txTab: Tab = new Tab {
      text = "Transactions"
      content = new TransactionsPane().view
    }

    val genTab: Tab = new Tab {
      text = "Generators"
      content = new GeneratorsPane().view
    }

    tabs = Seq(psbtsTab, txTab, genTab)

    tabClosingPolicy = TabClosingPolicy.Unavailable
  }

  private val model = new HomeGUIModel()

  private val borderPane = new BorderPane {
    top = AppMenuBar.menuBar
    center = tabPane
    bottom = statusLabel
  }

  private val rootView = new StackPane {
    children = Seq(borderPane, glassPane)
  }

  private val homeScene: Scene = new Scene(800, 400) {
    root = rootView
    stylesheets = GlobalData.currentStyleSheets
  }

  stage = new JFXApp.PrimaryStage {
    title = "PSBT Toolkit"
    scene = homeScene
    icons.add(new Image("/icons/psbt-toolkit.png"))
  }

  private val taskRunner = new TaskRunner(resultArea, glassPane)
  model.taskRunner = taskRunner
}
