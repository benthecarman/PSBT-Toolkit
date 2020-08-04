package org.psbttoolkit.gui.crypto

import org.bitcoins.crypto.{CryptoUtil, NetworkElement, StringFactory}
import scodec.bits.ByteVector

sealed abstract class HashAlgo {
  def hash(bytes: ByteVector): NetworkElement
}

object HashAlgo extends StringFactory[HashAlgo] {

  final case object SHA256 extends HashAlgo {
    override def toString: String = "SHA256"

    override def hash(bytes: ByteVector): NetworkElement = {
      CryptoUtil.sha256(bytes)
    }
  }

  final case object DoubleSHA256 extends HashAlgo {
    override def toString: String = "Double SHA256"

    override def hash(bytes: ByteVector): NetworkElement = {
      CryptoUtil.doubleSHA256(bytes)
    }
  }

  final case object RipeMD160 extends HashAlgo {
    override def toString: String = "RipeMD160"

    override def hash(bytes: ByteVector): NetworkElement = {
      CryptoUtil.ripeMd160(bytes)
    }
  }

  final case object Hash160 extends HashAlgo {
    override def toString: String = "Hash160"

    override def hash(bytes: ByteVector): NetworkElement = {
      CryptoUtil.sha256Hash160(bytes)
    }
  }

  final case object SHA1 extends HashAlgo {
    override def toString: String = "SHA1 (insecure)"

    override def hash(bytes: ByteVector): NetworkElement = {
      CryptoUtil.sha1(bytes)
    }
  }

  val all: Vector[HashAlgo] =
    Vector(SHA256, DoubleSHA256, RipeMD160, Hash160, SHA1)

  val names: Vector[String] = all.map(_.toString)

  def fromString(str: String): HashAlgo = {
    all.find(_.toString.toLowerCase == str.toLowerCase()).get
  }
}
