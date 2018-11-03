package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.Keyed

data class Sicop(
    val mnemonic: String,
    val opcode: String,
    val format: Int
) : Keyed {
  override fun key(): String = mnemonic
  override fun toString(): String = "$mnemonic\t$opcode\t$format"
}