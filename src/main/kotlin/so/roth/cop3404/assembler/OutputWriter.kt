package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.DataOp
import so.roth.cop3404.assembler.grammar.Instruction
import java.io.File

class OutputWriter(
    private val name: String,
    private val outputLines: ArrayList<OutputLine>,
    private val errors: HashMap<Int, AssemblyException>
) {

  fun writeLst() {
    File("$name.lst").printWriter().use { out ->
      out.println("""
      *********************************************
      Rothanak So: SIC/XE assembler
      *********************************************
      ASSEMBLER REPORT
      ----------------
           Loc   Object Code       Source Code
           ---   -----------       -----------
       """.trimIndent())

      // Print the assembler report with any errors
      outputLines.forEach { outputLine ->
        out.println(outputLine)

        // Print any errors beneath the line
        errors[outputLine.number]?.let {
          out.println("********** ERROR: ${it.message}")
        }
      }
    }
  }

  fun writeObj() {
    File("$name.obj").printWriter().use { out ->
      if (errors.isEmpty()) {
        val assembledLines = outputLines.filterIsInstance<AssembledLine>()
            .sortedWith(compareBy({ it.address }, { it.number }))

        // Find the index of the last RESW/RESB
        val last = assembledLines.indexOfLast { assembledLine ->
          (assembledLine.line.command as? Instruction)?.let {
            (it.operation as? DataOp)?.let { op -> op.name == "RESW" || op.name == "RESB" }
          } == true
        }

        // Print the starting address and address of the loader
        val startAddress = String.format("%06X", assembledLines[0].address and 0xFFFFFF)
        out.println(startAddress)
        out.println("000000")

        // Print each object code, with ! for RESW/RESB
        for ((i, assembledLine) in assembledLines.withIndex()) {
          assembledLine.obj?.let { out.println(it) }
          (assembledLine.line.command as? Instruction)?.let {
            (it.operation as? DataOp)?.let { op ->
              if (op.name == "RESW" || op.name == "RESB") {
                assembledLines.getOrNull(i + 1)?.let {
                  out.println("!")
                  out.println(String.format("%06X", it.address and 0xFFFFFF))
                  out.println(if (i != last) "000000" else startAddress)
                }
              }
            }
          }
        }

        // End the file with a !
        out.println("!")
      }
    }
  }
}