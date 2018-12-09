package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.Directive
import so.roth.cop3404.assembler.grammar.Instruction
import java.io.File
import java.util.*

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

  // Errors will be collected and reported
  val errors = Hashtable<Int, AssemblyException>()

  // The symbol table stores addresses of labelled lines
  val symbolStore = SymbolTable(numLines)

  // Begin pass 1 assembly
  val addressAssigner = AddressAssigner()
  val parser = Parser(sicOpsTable)
  val output = ArrayList<SourceLine>()
  inputLines.forEachIndexed { index, ln ->
    val lineNum = index + 1

    // Calculate the relative address
    try {
      val line = parser.parse(ln) ?: ln.let {
        output.add(CommentLine(lineNum, ln))
        return@forEachIndexed
      }

      val address = when (line.command) {
        is Directive -> addressAssigner.onDirective(line.command)
        is Instruction -> addressAssigner.onInstruction(line.command)
      }

      // Add the symbol to the symbol table
      try {
        symbolStore.store(address, line)
      } catch (e: AssemblyException) {
        errors[lineNum] = e
      }

      output.add(AddressedLine(index + 1, address, line))
    } catch (e: AssemblyException) {
      errors[lineNum] = e
    }
  }

  // Perform second pass
  val absoluteTable = symbolStore.toAbsolute(addressAssigner.useStartAddresses)
  val objectCodeAssembler = ObjectCodeAssembler(absoluteTable)
  val outputLines = ArrayList<OutputLine>()
  output.forEach { s ->
    outputLines.add(when (s) {
      is CommentLine -> CommentOutput(s.number, s.comment)
      is AddressedLine -> {
        val currentAddress = absoluteTable.insertAddress(s.address, s.line)
        val obj = try {
          when (val command = s.line.command) {
            is Directive -> objectCodeAssembler.onDirective(command).run { null }
            is Instruction -> objectCodeAssembler.onInstruction(command, currentAddress)
          }
        } catch (e: Exception) {
          when (e) {
            is AssemblyException -> errors[s.number] = e
            else -> println(e.message)
          }
          null
        }
        AssembledLine(s.number, currentAddress, obj, s.line)
      }
    })
  }

  println(
      """*********************************************
Rothanak So: SIC/XE assembler
*********************************************
ASSEMBLER REPORT
----------------
     Loc   Object Code       Source Code
     ---   -----------       -----------""")

  // Print the assembler report with any errors
  for (outputLine in outputLines) {
    println(outputLine)

    // Print any errors beneath the line
    errors[outputLine.number]?.let {
      println("********** ERROR: ${it.message}")
    }
  }
}
