package org.psbttoolkit.gui.transactions.types

import org.bitcoins.crypto.StringFactory

sealed abstract class AddressType {
  def shortName: String
}

object AddressType extends StringFactory[AddressType] {

  final case object LegacyAddressType extends AddressType {
    val shortName: String = "Legacy Address"
  }

  final case object P2SHAddressType extends AddressType {
    val shortName: String = "P2SH Address"
  }

  final case object Bech32AddressType extends AddressType {
    val shortName: String = "Bech32 Address"
  }

  val all: Vector[AddressType] =
    Vector(LegacyAddressType, P2SHAddressType, Bech32AddressType)

  val names: Vector[String] = all.map(_.shortName)

  def fromString(str: String): AddressType = {
    all.find(_.toString.toLowerCase == str.toLowerCase()).get
  }
}
