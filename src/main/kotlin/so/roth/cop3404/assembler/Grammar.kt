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
    return String.format("%-8s %-11s %s", mnemonic, operand, comment)
  }
}

data class Instruction(
    val modifier: String, // todo nullable
    override val mnemonic: String,
    val special: String,
    val operand: String,
    val comment: String
) : Command() {
  override fun toString(): String {
    return String.format("%-8s %-11s %s", modifier + mnemonic, special + operand, comment);
  }
}

data class LabeledInstruction (
    val label: String,
    val instruction: Command
) : Line(), Keyed {
  override fun key(): String = label
  override fun toString(): String {
    return String.format("%-7s %s", label, instruction);
  }
}

data class AddressedLine (
    val address: Int,
    val line: Line
) {
  override fun toString(): String {
    return String.format("%04X   %s", address, when(line) {
      is LabeledInstruction -> line
      else -> String.format("%7s %s", "", line)
    })
  }
}