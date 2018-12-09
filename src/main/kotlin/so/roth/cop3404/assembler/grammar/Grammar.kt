package so.roth.cop3404.assembler.grammar

import so.roth.cop3404.assembler.hash.Keyed

sealed class Command
data class Directive(
    val name: String,
    val operand: String
) : Command() {
  override fun toString(): String {
    return "%-8s %-11s".format(name, operand)
  }
}

data class Instruction(
    val modifier: String?,
    val operation: Operation,
    val special: String?,
    val operand: Operand
) : Command() {
  override fun toString(): String {
    return "%-8s %-11s".format((modifier ?: "") + operation, (special ?: "") + operand)
  }
}

sealed class Operation
data class DataOp(val name: String) : Operation() {
  override fun toString() = name
}

data class SicOp(
    val mnemonic: String,
    val opcode: String,
    val format: Int
) : Operation(), Keyed {
  override fun key(): String = mnemonic
  override fun toString(): String = mnemonic.replace(Regex("[^a-zA-Z]+"), "")
}

data class UnsupportedOp(val operation: String) : Operation() {
  override fun toString() = operation
}