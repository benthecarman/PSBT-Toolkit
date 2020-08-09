package org.psbttoolkit.gui.crypto

import org.bitcoins.crypto.StringFactory

sealed abstract class SignatureSchema

object SignatureSchema extends StringFactory[SignatureSchema] {

  final case object ECDSA extends SignatureSchema
  final case object Schnorr extends SignatureSchema
  final case object AdaptorSign extends SignatureSchema

  val all: Vector[SignatureSchema] = Vector(ECDSA, Schnorr, AdaptorSign)

  val names: Vector[String] = all.map(_.toString)

  def fromString(str: String): SignatureSchema = {
    all.find(_.toString.toLowerCase == str.toLowerCase()).get
  }
}
