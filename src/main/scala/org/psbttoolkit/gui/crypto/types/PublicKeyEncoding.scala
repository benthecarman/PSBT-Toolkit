package org.psbttoolkit.gui.crypto.types

import org.bitcoins.crypto.StringFactory

sealed abstract class PublicKeyEncoding

object PublicKeyEncoding extends StringFactory[PublicKeyEncoding] {

  final case object ECDSA extends PublicKeyEncoding
  final case object Schnorr extends PublicKeyEncoding

  val all: Vector[PublicKeyEncoding] = Vector(ECDSA, Schnorr)

  val names: Vector[String] = all.map(_.toString)

  def fromString(str: String): PublicKeyEncoding = {
    all.find(_.toString.toLowerCase == str.toLowerCase()).get
  }
}
