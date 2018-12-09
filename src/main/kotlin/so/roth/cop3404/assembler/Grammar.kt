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

sealed class Command

data class Directive(
    val name: String,
    val operand: String
) : Command() {
  override fun toString(): String {
    return "%-8s %-11s".format(name, operand)
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

sealed class Operand

data class RegisterOperand(val r1: String, val r2: String) : Operand() {
  override fun toString() = "$r1,$r2"
}

data class NumberOperand(val value: Int) : Operand() {
  override fun toString() = String.format("%X", value and 0xFFFFFF)
}

open class LabelOperand(val label: String) : Operand() {
  override fun toString() = label
}

class IndexedOperand(label: String) : LabelOperand(label) {
  override fun toString() = "$label,X"
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