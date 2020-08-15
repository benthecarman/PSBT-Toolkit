package org.psbttoolkit.gui.generators.types

sealed abstract class TransactionGenType

object TransactionGenType {

  final case object ArbitraryTransaction extends TransactionGenType {
    override def toString: String = "Arbitrary Transaction"
  }

  final case object BaseTransaction extends TransactionGenType {
    override def toString: String = "Base Transaction"
  }

  final case object WitnessTransaction extends TransactionGenType {
    override def toString: String = "Witness Transaction"
  }

  final case object UnsignedTransaction extends TransactionGenType {
    override def toString: String = "Unsigned Transaction"
  }

  val all: Vector[TransactionGenType] =
    Vector(ArbitraryTransaction,
           BaseTransaction,
           WitnessTransaction,
           UnsignedTransaction)

  val names: Vector[String] = all.map(_.toString)

  def fromStringOpt(str: String): Option[TransactionGenType] = {
    all.find(_.toString.toLowerCase == str.toLowerCase)
  }

  def fromString(str: String): TransactionGenType = {
    fromStringOpt(str).get
  }
}
