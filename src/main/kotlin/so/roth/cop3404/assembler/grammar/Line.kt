package so.roth.cop3404.assembler.grammar

import so.roth.cop3404.assembler.hash.Keyed

data class Line(
    val label: String?,
    val command: Command,
    val comment: String?
) : Keyed {
  override fun key(): String = label ?: throw IllegalStateException("Key is null")
  override fun toString(): String {
    return "%-7s %s %s".format(label ?: "", command, comment ?: "")
  }
}