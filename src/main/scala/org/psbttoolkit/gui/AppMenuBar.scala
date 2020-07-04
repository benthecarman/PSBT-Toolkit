package org.psbttoolkit.gui

import scalafx.scene.control.{Menu, MenuBar, MenuItem}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}

object AppMenuBar {

  val menuBar: MenuBar = new MenuBar {
    menus = List(FileMenu.fileMenu, ViewMenu.viewMenu, HelpMenu.helpMenu)
  }
}

private object FileMenu {

  private val loadPSBT = new MenuItem("Load from File") {
    accelerator =
      new KeyCodeCombination(KeyCode.O, KeyCombination.ControlDown) // CTRL + O
    onAction = _ => println("load")
  }

  val fileMenu: Menu = new Menu("File") {
    items = List(loadPSBT)
  }
}

private object ViewMenu {

  private val themes = new MenuItem("Themes") {
    onAction = _ => println("themes") //todo
  }

  val viewMenu: Menu = new Menu("View") {
    items = List(themes)
  }
}

private object HelpMenu {

  private val about = new MenuItem("About") {
    onAction = _ => println("About") //todo
  }

  val helpMenu: Menu = new Menu("Help") {
    items = List(about)
  }
}
