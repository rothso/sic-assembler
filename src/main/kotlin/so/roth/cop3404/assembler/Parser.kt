package so.roth.cop3404.assembler

class Parser(private val table: SicOpsTable) {

  fun parse(line: String): Line? {
    // Check if line is just a comment
    if (line.trim()[0] == '.')
      return null

    // Parse the line into a command
    val ln = line.padEnd(32, ' ')
    val label = ln.substring(0, 8).trim().takeIf { it.isNotBlank() }
    val modifier = "${ln[9]}".trim().takeIf { it.isNotBlank() }
    val mnemonic = ln.substring(10, 17).trim()
    val special = "${ln[18]}".trim().takeIf { it.isNotBlank() }
    val operand = ln.substring(19, 29).trim()
    val comment = ln.substring(31).trim().takeIf { it.isNotBlank() }

    val command = when (mnemonic) {
      "START", "BASE", "END" -> Directive(mnemonic, operand)
      else -> {
        val operation = when (mnemonic) {
          "WORD", "BYTE", "RESW", "RESB" -> DataOp(mnemonic)
          else -> table.getSicOp(mnemonic, modifier) ?: throw InvalidMnemonicException(mnemonic)
        }
        Instruction(modifier, operation, special, parseOperand(operand))
      }
    }

    return Line(label, command, comment)
  }

  private fun parseOperand(operand: String): Operand {
    return when {
      operand.matches(Regex("[A-Z]+,X")) -> IndexedOperand(operand.split(",")[0])
      operand.toIntOrNull(16) != null -> NumberOperand(operand.toInt(16))
      operand.matches(Regex("[A-Z]{1,2},[A-Z]{1,2}")) -> {
        val registers = operand.split(",")
        RegisterOperand(registers[0], registers[1])
      }
      else -> LabelOperand(operand)
    }
  }
}