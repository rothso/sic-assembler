package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.Directive
import so.roth.cop3404.assembler.grammar.Instruction
import so.roth.cop3404.assembler.grammar.Line
import java.io.File

// Pass 1 artifacts
sealed class SourceLine

data class CommentLine(val comment: String) : SourceLine()
data class AddressedLine(val address: Address, val line: Line) : SourceLine()

// Pass 2 artifacts
sealed class OutputLine

data class CommentOutput(val comment: String) : OutputLine() {
  override fun toString() = comment
}

data class AssembledLine(val address: Int, val obj: ObjectCode?, val line: Line) : OutputLine() {
  override fun toString(): String {
    return String.format("%06X  %-12s  %s", address, obj ?: "", line)
  }
}

/**
 * SIC/XE Assembler
 *
 * @author Rothanak So
 */
fun main(args: Array<String>) {
  val sicOpsTable = SicOpsTable("src/main/resources/SICOPS.txt")

  val inputFile = File(args[0])
  val inputLines = inputFile.readLines().filter { it.isNotBlank() }
  val numLines = inputLines.count()

  // Errors will be collected and reported at the top
  val errors = ArrayList<AssemblyException>()

  // The symbol table stores addresses of labelled lines
  val symbolStore = SymbolTable(numLines)

  // Begin pass 1 assembly
  val addressAssigner = AddressAssigner()
  val parser = Parser(sicOpsTable)
  val output = ArrayList<SourceLine>()
  inputLines.forEach { ln ->
    // Calculate the relative address
    try {
      val line = parser.parse(ln) ?: ln.let {
        output.add(CommentLine(ln))
        return@forEach
      }

      val address = when (line.command) {
        is Directive -> addressAssigner.onDirective(line.command)
        is Instruction -> addressAssigner.onInstruction(line.command)
      }

      // Add the symbol to the symbol table
      try {
        symbolStore.store(address, line)
      } catch (e: AssemblyException) {
        errors.add(e)
      }

      output.add(AddressedLine(address, line))
    } catch (e: AssemblyException) {
      errors.add(e)
    }
  }

  // Perform second pass
  val absoluteTable = symbolStore.toAbsolute(addressAssigner.useStartAddresses)
  val objectCodeAssembler = ObjectCodeAssembler(absoluteTable)
  val outputLines = ArrayList<OutputLine>()
  output.forEach { s ->
    when (s) {
      is CommentLine -> CommentOutput(s.comment)
      is AddressedLine -> try {
        val currentAddress = absoluteTable.insertAddress(s.address, s.line)
        val obj = when (val command = s.line.command) {
          is Directive -> objectCodeAssembler.onDirective(command).run { null }
          is Instruction -> objectCodeAssembler.onInstruction(command, currentAddress)
        }

        val assembledLine = AssembledLine(currentAddress, obj, s.line)
        outputLines.add(assembledLine)
      } catch (e: Exception) {
        println(e)
      }
    }
  }


  // Print the errors followed by the assembler report
  errors.forEach { println("********** ERROR: ${it.message}") }
  println()

  for (outputLine in outputLines) {
    println(outputLine)
  }
}
