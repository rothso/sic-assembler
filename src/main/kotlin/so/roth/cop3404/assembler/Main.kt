package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.HashTable
import java.io.File

sealed class SourceLine
data class CommentLine(val comment: String) : SourceLine()
data class AddressedLine(val address: Address, val line: Line) : SourceLine()

/**
 * SIC/XE Assembler
 *
 * @author Rothanak So
 */
fun main(args: Array<String>) {
  val sicOpsTable = readSicops()

  val inputFile = File(args[0])
  val inputLines = inputFile.readLines().filter { it.isNotBlank() }
  val numLines = inputLines.count()

  // Begin pass 1 assembly
  val output = ArrayList<SourceLine>()
  val errors = ArrayList<AssemblyException>()

  val addressTransformer = Addresser(sicOpsTable)
  val symbolStore = SymbolTable(numLines)
  val parser = Parser()

  inputLines.forEach { ln ->
    val line = parser.parse(ln) ?: ln.let {
      output.add(CommentLine(ln))
      return@forEach
    }

    // Calculate the relative address
    try {
      val address = when (line.command) {
        is Directive -> addressTransformer.onDirective(line.command)
        is Instruction -> addressTransformer.onInstruction(line.command)
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

  // Print the errors first
  errors.forEach { println("ERROR: ${it.message}") }
  println()

  // Print the source code with addresses
  val useOffsets = addressTransformer.useStartAddresses
  output.forEach { s ->
    println(when (s) {
      is CommentLine -> s.comment
      is AddressedLine -> {
        val realAddress = s.address.relative + useOffsets[s.address.block.number]
        String.format("%04X   ", realAddress) + s.line
      }
    })
  }
  println()

  // Print the contents of the symbol table
  symbolStore.printTable()
}

fun readSicops(): HashTable<Sicop> {
  val sicopsFile = File("src/main/resources/SICOPS.txt")
  val inputLines = sicopsFile.readLines().filter { it.isNotBlank() }
  val numLines = inputLines.count()

  // Lines must describe a mnemonic and not a register
  val operation = Regex("^([+*]?[A-Z]+)\\s*([0-9A-F]{2})\\s*(\\d)\\s*\\d\$")

  // Read and store sicops information
  val sicOpsTable = HashTable<Sicop>(numLines)
  inputLines.forEach { line ->
    operation.matchEntire(line)?.destructured?.let { (mnemonic, opcode, format) ->
      val sicop = Sicop(mnemonic, opcode, format.toInt())
      sicOpsTable.insert(sicop)
    }
  }

  return sicOpsTable
}
