package org.psbttoolkit.gui.generators

sealed abstract class PSBTGenType

object PSBTGenType {

  final case object ArbitraryPSBT extends PSBTGenType {
    override def toString: String = "Arbitrary PSBT"
  }

  final case object FinalizedPSBT extends PSBTGenType {
    override def toString: String = "Finalized PSBT"
  }

  final case object FullNonFinalizedPSBT extends PSBTGenType {
    override def toString: String = "Non-Finalized PSBT"
  }

  final case object PSBTWithUnknowns extends PSBTGenType {
    override def toString: String = "PSBT With Unknowns"
  }

  final case object PSBTWithUnknownVersion extends PSBTGenType {
    override def toString: String = "PSBT With Unknown Version"
  }

  val all: Vector[PSBTGenType] = Vector(ArbitraryPSBT,
                                        FinalizedPSBT,
                                        FullNonFinalizedPSBT,
                                        PSBTWithUnknowns,
                                        PSBTWithUnknownVersion)

  val names: Vector[String] = all.map(_.toString)

  def fromStringOpt(str: String): Option[PSBTGenType] = {
    all.find(_.toString.toLowerCase == str.toLowerCase())
  }

  def fromString(str: String): PSBTGenType = {
    fromStringOpt(str).get
  }
}
