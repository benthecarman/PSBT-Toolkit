package org.psbttoolkit.gui.transactions.dialog

import org.bitcoins.core.currency.Satoshis
import org.bitcoins.core.number.{Int32, UInt32}
import org.bitcoins.core.protocol.script.{EmptyScriptSignature, ScriptPubKey}
import org.bitcoins.core.protocol.transaction._
import org.bitcoins.crypto.DoubleSha256DigestBE
import org.psbttoolkit.gui.GlobalData
import scalafx.Includes._
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.Node
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, HBox, VBox}
import scalafx.stage.Window

object ConstructTransactionDialog {

  def showAndWait(parentWindow: Window): Option[Transaction] = {
    val dialog = new Dialog[Option[Transaction]]() {
      initOwner(parentWindow)
      title = "Construct Transaction"
    }

    dialog.dialogPane().buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
    dialog.dialogPane().stylesheets = GlobalData.currentStyleSheets
    dialog.resizable = true

    val versionTF = new TextField() {
      text = "2"
    }
    val lockTimeTF = new TextField() {
      text = "0"
    }

    val verticalSeparator: Separator = new Separator() {
      orientation = Orientation.Vertical
    }

    val inputMap: scala.collection.mutable.Map[Int, (TextField, TextField)] =
      scala.collection.mutable.Map.empty

    var nextInputRow: Int = 2
    val inputGrid: GridPane = new GridPane {
      padding = Insets(top = 10, right = 10, bottom = 10, left = 10)
      hgap = 5
      vgap = 5
    }

    def addInputRow(): Unit = {

      val outPointTF = new TextField() {
        promptText = "Previous Transaction Id"
      }
      val voutTF = new TextField() { promptText = "Output Index (vout)" }
      val row = nextInputRow
      inputMap.addOne((row, (outPointTF, voutTF)))

      inputGrid.add(outPointTF, 0, row)
      inputGrid.add(voutTF, 1, row)

      nextInputRow += 1
      dialog.dialogPane().getScene.getWindow.sizeToScene()
    }

    val outputMap: scala.collection.mutable.Map[Int, (TextField, TextField)] =
      scala.collection.mutable.Map.empty

    var nextOutputRow: Int = 2
    val outputGrid: GridPane = new GridPane {
      padding = Insets(10, 10, 10, 10)
      hgap = 5
      vgap = 5
    }

    // FIXME need to move all subsequent rows up
    def removeOutputRow(index: Int, button: Button): Unit = {
      val nodes = outputMap(index)
      outputGrid.children.removeAll(nodes._1, nodes._2, button)
      outputMap.remove(index)
      nextOutputRow -= 1
      dialog.dialogPane().getScene.getWindow.sizeToScene()
    }

    def addOutputRow(): Unit = {

      val valueTF = new TextField() { promptText = "Value (in sats)" }
      val spkTF = new TextField() { promptText = "Script PubKey" }
      val row = nextOutputRow
      outputMap.addOne((row, (valueTF, spkTF)))

      outputGrid.add(valueTF, 0, row)
      outputGrid.add(spkTF, 1, row)

      nextOutputRow += 1
      dialog.dialogPane().getScene.getWindow.sizeToScene()
    }

    addInputRow()
    addInputRow()
    addOutputRow()
    addOutputRow()

    val addInputButton: Button = new Button("+") {
      onAction = _ => addInputRow()
    }
    val addOutputButton: Button = new Button("+") {
      onAction = _ => addOutputRow()
    }

    dialog.dialogPane().content = new VBox() {
      padding = Insets(20, 10, 10, 10)
      spacing = 10

      val globalData: Node = new HBox() {
        spacing = 10
        alignment = Pos.Center
        children = Vector(new Label("Version"),
                          versionTF,
                          new Label("nLockTime"),
                          lockTimeTF)
      }

      val inputsAndOutputs: HBox = new HBox {
        spacing = 10

        val inputs: Node = new VBox {
          alignment = Pos.TopCenter

          val label: HBox = new HBox {
            spacing = 10
            children = Vector(new Label("Inputs"), addInputButton)
          }
          children = Vector(label, inputGrid)
        }

        val outputs: Node = new VBox {
          alignment = Pos.TopCenter

          val label: HBox = new HBox {
            spacing = 10
            children = Vector(new Label("Outputs"), addOutputButton)
          }
          children = Vector(label, outputGrid)
        }

        children = Vector(inputs, verticalSeparator, outputs)
      }

      children = Vector(globalData, new Separator(), inputsAndOutputs)
    }

    // Enable/Disable OK button depending on whether all data was entered.
    val okButton = dialog.dialogPane().lookupButton(ButtonType.OK)
    // Simple validation that sufficient data was entered
    okButton.disable <== versionTF.text.isEmpty || lockTimeTF.text.isEmpty

    // When the OK button is clicked, convert the result to a T.
    dialog.resultConverter = dialogButton =>
      if (dialogButton == ButtonType.OK) {

        val version = Int32(versionTF.text.value.toLong)
        val nLockTime = UInt32(lockTimeTF.text.value.toLong)

        val inputStrs = inputMap.values
        val inputs = inputStrs.flatMap {
          case (txIdStr, voutStr) =>
            if (txIdStr.text.value.nonEmpty && voutStr.text.value.nonEmpty) {
              val txIdBE = DoubleSha256DigestBE(txIdStr.text.value)
              val vout = UInt32(voutStr.text.value.toLong)
              val outPoint = TransactionOutPoint(txIdBE, vout)

              Some(
                TransactionInput(outPoint,
                                 EmptyScriptSignature,
                                 TransactionConstants.disableRBFSequence))
            } else {
              None
            }
        }.toVector

        val outputStrs = outputMap.values
        val outputs = outputStrs.flatMap {
          case (valueStr, spkStr) =>
            if (valueStr.text.value.nonEmpty && spkStr.text.value.nonEmpty) {
              val value = Satoshis(valueStr.text.value.toLong)
              val spk = ScriptPubKey(spkStr.text.value)

              Some(TransactionOutput(value, spk))
            } else {
              None
            }
        }.toVector

        val transaction =
          BaseTransaction(version, inputs, outputs, nLockTime)

        Some(transaction)
      } else None

    dialog.showAndWait() match {
      case Some(Some(tx: Transaction)) =>
        Some(tx)
      case Some(_) | None => None
    }
  }
}
