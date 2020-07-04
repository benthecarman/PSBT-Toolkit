package org.psbttoolkit.gui.settings

import javafx.event.ActionEvent
import org.psbttoolkit.gui.GlobalData
import scalafx.scene.control.CheckBox
import scalafx.scene.layout.StackPane

class SettingsPane {

  private val themeCheckBox = new CheckBox {
    text = "Dark Theme"
    selected = GlobalData.darkThemeEnabled
    onAction = (_: ActionEvent) => {
      if (!selected.value) {
        Themes.DarkTheme.undoTheme
      } else {
        Themes.DarkTheme.applyTheme
      }
      ()
    }
  }

  val view: StackPane = new StackPane {
    children = Seq(
      themeCheckBox
    )
  }
}
