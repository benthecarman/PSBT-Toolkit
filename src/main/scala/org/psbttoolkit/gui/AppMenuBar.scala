package org.psbttoolkit.gui

import java.nio.file.Files

import org.bitcoins.core.psbt.PSBT
import org.psbttoolkit.gui.util.FileUtil
import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scodec.bits.ByteVector

object AppMenuBar {

  def menuBar(model: HomeGUIModel): MenuBar =
    new MenuBar {
      menus = List(new FileMenu(model).fileMenu,
                   new ViewMenu(model).viewMenu,
                   new HelpMenu(model).helpMenu)
    }
}

private class FileMenu(model: HomeGUIModel) {

  private val loadPSBT = new MenuItem("Load PSBT") {
    accelerator =
      new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown) // CTRL + O
    onAction = _ => {
      val fileChooser = FileUtil.getPSBTFileChooser("Load PSBT from file")

      val selectedFile = fileChooser.showOpenDialog(model.parentWindow.value)

      if (selectedFile != null) {
        val byteArray = Files.readAllBytes(selectedFile.toPath)
        val psbt = PSBT(ByteVector(byteArray))
        model.setPSBTResult(psbt.hex)
      }
    }
  }

  private val exportPSBT = new MenuItem("Export PSBT") {
    accelerator =
      new KeyCodeCombination(KeyCode.S, KeyCombination.ControlDown) // CTRL + S
    onAction = _ => {
      model.getPSBTOpt match {
        case Some(psbt) =>
          val fileChooser = FileUtil.getPSBTFileChooser("Export PSBT")
          val selectedFile =
            fileChooser.showSaveDialog(model.parentWindow.value)

          if (selectedFile != null) {
            Files.write(selectedFile.toPath, psbt.bytes.toArray)
          }
        case None =>
          throw new RuntimeException("Not a valid PSBT!")
      }
    }
  }

  val fileMenu: Menu =
    new Menu("File") {
      items = List(loadPSBT, exportPSBT)
    }
}

private class ViewMenu(model: HomeGUIModel) {

  private val themes = new MenuItem("Themes") {
    onAction = _ => println("themes") //todo
  }

  val viewMenu: Menu = new Menu("View") {
    items = List(themes)
  }
}

private class HelpMenu(model: HomeGUIModel) {

  private val about =
    new MenuItem("About") {
      accelerator = new KeyCodeCombination(KeyCode.F1) // F1
      onAction = _ => model.onAbout()
    }

  val helpMenu: Menu =
    new Menu("Help") {
      items = List(about)
    }
}
