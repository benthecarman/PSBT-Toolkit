package org.psbttoolkit.gui.transactions

import org.bitcoins.crypto.StringFactory

sealed abstract class PubKeyScriptType

object PubKeyScriptType extends StringFactory[PubKeyScriptType] {

  final case object P2PK extends PubKeyScriptType
  final case object P2PKH extends PubKeyScriptType
  final case object P2WPKH extends PubKeyScriptType

  val all: Vector[PubKeyScriptType] = Vector(P2PK, P2PKH, P2WPKH)

  val names: Vector[String] = all.map(_.toString)

  def fromString(str: String): PubKeyScriptType = {
    all.find(_.toString.toLowerCase == str.toLowerCase()).get
  }
}
