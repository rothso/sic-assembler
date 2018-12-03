package so.roth.cop3404.assembler

class Parser {

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
      else -> Instruction(modifier, mnemonic, special, operand)
    }

    return Line(label, command, comment)
  }
}