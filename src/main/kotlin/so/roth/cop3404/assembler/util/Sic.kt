package so.roth.cop3404.assembler.util

fun sizeBytes(operand: String): Int {
  return if (operand.startsWith("C'") && operand.endsWith("'")) {
    operand.length - 3
  } else {
    (operand.toInt().toString(16).length + 1) / 2
  }
}