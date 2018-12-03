package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.Keyed

data class Block(val number: Int, val name: String)

data class Address(val relative: Int, val block: Block)

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

sealed class Command {
  abstract val mnemonic: String
  abstract val operand: String
}

data class Directive(
    override val mnemonic: String,
    override val operand: String
) : Command() {
  override fun toString(): String {
    return "%-8s %-11s".format(mnemonic, operand)
  }
}

data class Instruction(
    val modifier: String?,
    override val mnemonic: String,
    val special: String?,
    override val operand: String
) : Command() {
  override fun toString(): String {
    return "%-8s %-11s".format((modifier ?: "") + mnemonic, (special ?: "") + operand)
  }
}