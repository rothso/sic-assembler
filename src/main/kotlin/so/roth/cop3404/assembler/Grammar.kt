package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.Keyed

sealed class Line

sealed class Command : Line() {
  abstract val mnemonic: String
}

data class Directive(
  override val mnemonic: String,
  val operand: String,
  val comment: String
) : Command() {
  override fun toString(): String {
    return "$mnemonic\t$operand\t$comment"
  }
}

data class Instruction(
    val modifier: String, // todo nullable
    override val mnemonic: String,
    val special: Char,
    val operand: String,
    val comment: String
) : Command() {
  override fun toString(): String {
    return "$modifier$mnemonic\t$special$operand\t$comment"
  }
}

data class LabeledInstruction (
    val label: String,
    val instruction: Command
) : Line(), Keyed {
  override fun key(): String = label
  override fun toString(): String {
    return "$label\t$instruction"
  }
}

data class AddressedLine (
    val address: Int,
    val line: Line
) {
  override fun toString(): String {
    return "${address.toString(16).toUpperCase()}\t$line"
  }
}