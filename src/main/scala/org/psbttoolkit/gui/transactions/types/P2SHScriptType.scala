package org.psbttoolkit.gui.transactions.types

import org.bitcoins.crypto.StringFactory

sealed abstract class P2SHScriptType

object P2SHScriptType extends StringFactory[P2SHScriptType] {

  final case object P2SH extends P2SHScriptType
  final case object P2WSH extends P2SHScriptType

  val all: Vector[P2SHScriptType] = Vector(P2SH, P2WSH)

  val names: Vector[String] = all.map(_.toString)

  override def fromString(str: String): P2SHScriptType = {
    all.find(_.toString.toLowerCase == str.toLowerCase()).get
  }
}
