package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.Keyed

data class Symbol(val name: String, val number: Int) : Keyed {
  override fun key(): String = name
  override fun toString(): String = "$name $number"
}