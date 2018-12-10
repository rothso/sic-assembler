package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.*

class Parser(private val sicOps: SicOpsTable) {

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
      "START", "BASE", "NOBASE", "END", "USE", "LTORG" -> Directive(mnemonic, operand)
      "WORD", "BYTE", "RESW", "RESB" -> {
        Instruction(modifier, DataOp(mnemonic), special, parseOperand(operand))
      }
      else -> {
        val op = sicOps.getSicOp(mnemonic, modifier) ?: UnsupportedOp(mnemonic)
        Instruction(modifier, op, special, parseOperand(operand))
      }
    }

    return Line(label, command, comment)
  }

  private fun parseOperand(operand: String): Operand {
    return when (operand) {
      "" -> BlankOperand
      in Regex("-?[\\d]+") -> NumericOperand(operand.toInt())
      in Regex("[A-Z]+,X") -> IndexedOperand(operand.split(",")[0])
      in Regex("C'[A-Z0-9]+'") -> CharOperand(operand.substringAfter("'").substringBefore("'"))
      in Regex("X'[A-F0-9]+'") -> HexOperand(operand.substringAfter("'").substringBefore("'"))
      in Regex("A|X|L|PC|SW|B|S|T|F") -> operand.let { r1 ->
        Register1Operand(sicOps.getRegister(r1) ?: throw InvalidRegisterException(r1))
      }
      in Regex("(A|X|L|PC|SW|B|S|T|F),(A|X|L|PC|SW|B|S|T|F)") -> operand.split(",").let{ (r1, r2) ->
        val reg1 = sicOps.getRegister(r1) ?: throw InvalidRegisterException(r1)
        val reg2 = sicOps.getRegister(r2) ?: throw InvalidRegisterException(r2)
        Register2Operand(reg1, reg2)
      }
      in Regex("(A|X|L|PC|SW|B|S|T|F),[0-9]") -> operand.split(",").let { (r1, n) ->
        val reg1 = sicOps.getRegister(r1) ?: throw InvalidRegisterException(r1)
        RegisterNOperand(reg1, n.toInt())
      }
      in Regex("[A-Z0-9]+") -> LabelOperand(operand)
      else -> BadOperand(operand)
    }
  }

  private operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
}