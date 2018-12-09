package so.roth.cop3404.assembler.grammar

sealed class Operand

object BlankOperand : Operand() {
  override fun toString() = ""
}

data class Register1Operand(val r1: Register) : Operand() {
  override fun toString() = "$r1"
}

data class Register2Operand(val r1: Register, val r2: Register) : Operand() {
  override fun toString() = "$r1,$r2"
}

data class RegisterNOperand(val r1: Register, val n: Int) : Operand() {
  override fun toString() = "$r1,$n"
}

data class NumericOperand(val value: Int) : Operand() {
  override fun toString() = value.toString() // base 10
}

class IndexedOperand(label: String) : LabelOperand(label) {
  override fun toString() = "$label,X"
}

open class LabelOperand(val label: String) : Operand() {
  override fun toString() = label
}

data class CharOperand(val string: String) : Operand() {
  override fun toString() = "C'$string'"
}

data class HexOperand(val hexString: String) : Operand() {
  override fun toString() = "X'$hexString'"
}
