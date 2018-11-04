package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.HashTable
import so.roth.cop3404.assembler.hash.Node
import so.roth.cop3404.assembler.util.sizeBytes
import java.io.File

/**
 * Project 3 - Pass 1
 *
 * @author Rothanak So
 */

fun main(args: Array<String>) {
  val inputFile = File("src/main/resources/SICOPS.txt")
  val inputLines = inputFile.readLines().filter { it.isNotBlank() }
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

  // Begin pass 1 assembly
  val symbolTable = HashTable<LabeledInstruction>(numLines)
  val output = ArrayList<String>()
  val errors = ArrayList<AssemblyError>()
  var nextAddress = 0
  File("src/main/resources/input.txt").forEachLine {
    val line = it.trim()

    if (line[0] == '.') {
      // Line is a comment
      output.add(line)
    } else {
      // Parse into an instruction
      val instr = line.padEnd(32, ' ').let { ln ->
        val label = ln.substring(0, 8).trim()
        val modifier = (ln[9] + " ").trim()
        val mnemonic = ln.substring(10, 17).trim()
        val special = (ln[18] + " ").trim()
        val operand = ln.substring(19, 29).trim()
        val comment = ln.substring(31).trim()

        val command = when(mnemonic) {
          "START", "BASE", "END" -> Directive(mnemonic, operand, comment)
          else -> Instruction(modifier, mnemonic, special, operand, comment)
        }

        if (label.isNotBlank()) LabeledInstruction(label, command) else command
      }

      // Add to symbol table
      if (instr is LabeledInstruction) {
        if (symbolTable.find(instr.key()) != null) {
          errors.add(AssemblyError("Duplicate Label \"${instr.label}\""))
        } else {
          symbolTable.insert(instr)
        }
      }

      // Set the start address
      val unwrapped = if (instr is LabeledInstruction) instr.instruction else instr
      if (unwrapped is Directive) {
        if (unwrapped.mnemonic == "START") {
          nextAddress = unwrapped.operand.toInt(16)
        }
      }

      // Prepare to determine the address of the current line
      fun decorateAddress(ln: Line): AddressedLine = when (ln) {
        is LabeledInstruction -> decorateAddress(ln.instruction)
        is Directive -> AddressedLine(nextAddress, instr).also { ln.address = nextAddress }
        is Instruction -> {
          AddressedLine(nextAddress, instr).also {
            ln.address = nextAddress
            nextAddress += when (ln.mnemonic) {
              "WORD" -> 3
              "RESW" -> 3 * ln.operand.toInt()
              "BYTE" -> 1 * sizeBytes(ln.operand)
              "RESB" -> ln.operand.toInt()
              else -> with(sicOpsTable.find(ln.modifier + ln.mnemonic)) {
                if (this == null) {
                  errors.add(AssemblyError("Ignoring Invalid Mneomonic \"${ln.mnemonic}\""))
                  0
                } else format
              }
            }
          }
        }
      }

      // Print the addressed output immediately
      val addressed = decorateAddress(instr)
      output.add(addressed.toString())

      // If there was no label, store an error
      if (addressed.line !is LabeledInstruction) {
        errors.add(AssemblyError("Undefined Label At Address ${addressed.address}"))
      }
    }
  }

  // Print the outputs
  errors.forEach { println("ERROR: $it") }.also { println() }
  output.forEach(::println).also { println() }

  // Print the table
  System.out.printf("%-15s %-10s %-10s\n", "Table Location", "Label", "Address")
  symbolTable.forEachIndexed { index, node ->
    if (node is Node.Item<LabeledInstruction>) {
      with(node.item) {
        System.out.printf("%-15d %-10s %04X\n", index, label, instruction.address)
      }
    }
  }
}
