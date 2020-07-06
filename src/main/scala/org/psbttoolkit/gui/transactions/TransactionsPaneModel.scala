package org.psbttoolkit.gui.transactions

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.stream.ActorMaterializer
import akka.util.ByteString
import org.bitcoins.core.config.{MainNet, RegTest, TestNet3}
import org.bitcoins.core.protocol.transaction.Transaction
import org.bitcoins.core.util.FutureUtil
import org.bitcoins.server.SerializedTransaction
import org.psbttoolkit.gui.dialog.DecodeTransactionDialog
import org.psbttoolkit.gui.{GlobalData, TaskRunner}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.control.TextArea
import scalafx.stage.Window

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.Try

class TransactionsPaneModel(resultArea: TextArea) {
  var taskRunner: TaskRunner = _

  // Sadly, it is a Java "pattern" to pass null into
  // constructors to signal that you want some default
  val parentWindow: ObjectProperty[Window] =
    ObjectProperty[Window](null.asInstanceOf[Window])

  def setResult(str: String): Unit = {
    resultArea.text = str
  }

  def getTransactionOpt: Option[Transaction] = {
    Try(Transaction.fromHex(resultArea.text.value)).toOption
  }

  def broadcastTx()(implicit system: ActorSystem): Unit = {
    implicit val m: ActorMaterializer = ActorMaterializer.create(system)
    implicit val ec: ExecutionContextExecutor = m.executionContext

    taskRunner.run(
      caption = "Broadcast Transaction",
      op = getTransactionOpt match {
        case Some(tx) =>
          val url = GlobalData.network match {
            case MainNet =>
              "https://blockstream.info/api/tx"
            case TestNet3 =>
              "https://blockstream.info/testnet/api/tx"
            case RegTest =>
              throw new IllegalArgumentException(
                "Unable broadcast a regtest transaction")
          }

          val resultF = Http()
            .singleRequest(Post(url, tx.hex))
            .flatMap(response =>
              response.entity.dataBytes
                .runFold(ByteString.empty)(_ ++ _)
                .map(payload => payload.decodeString(ByteString.UTF_8)))

          resultF.map { result =>
            if (result != tx.txIdBE.hex) {
              throw new RuntimeException(result)
            }
          }
        case None =>
          throw new RuntimeException("Missing Transaction")
      }
    )
  }

  def decodeTransaction(): Unit = {
    val resultOpt = getTransactionOpt.map { tx =>
      val decoded = SerializedTransaction.decodeRawTransaction(tx)
      DecodeTransactionDialog.showAndWait(parentWindow.value, decoded)
    }

    taskRunner.run(
      caption = "Decode Transaction",
      op = getTransactionOpt match {
        case Some(_) =>
          resultOpt.map(_ => ())
        case None =>
          throw new RuntimeException("Missing Transaction")
      }
    )
  }
}
