package so.roth.cop3404.assembler

import java.io.File

fun main(args: Array<String>) {
  File("src/main/resources/input.txt").forEachLine {
    val line = it.trim()

    val comment: String
    if (line[0] == '.') {
      // Line is a comment
      comment = line
      println(comment)
    } else {
      // Line is an instruction
      val line = line.padEnd(32, ' ')
      val label = line.substring(0, 8).trim()
      val extended = line[9]
      val mnemonic = line.substring(10, 17).trim()
      val special = line[18]
      val operand = line.substring(19, 29).trim()
      comment = line.substring(31).trim()
      println("$label $extended$mnemonic $special$operand  $comment")
    }
  }
}