package so.roth.cop3404.assembler

import java.io.File

fun main(args: Array<String>) {
  val pgmFile = File("src/main/resources/input.txt")
  println(pgmFile.absoluteFile)
  val inputLines = pgmFile.readLines().filter { it.isNotBlank() }
  val symbolTable = SymbolTable(inputLines.count())

  pgmFile.forEachLine {
    val line = it.trim()

    val comment: String
    if (line[0] == '.') {
      // Line is a comment
      comment = line
      println(comment)
    } else {
      // Line is an instruction
      val line = line.padEnd(32, ' ')
      val label = line.substring(0, 8)
      val extended = line[9]
      val mnemonic = line.substring(10, 17)
      val special = line[18]
      val operand = line.substring(19, 29)
      comment = line.substring(31)
      println("$label $extended$mnemonic $special$operand  $comment")
    }
  }
}