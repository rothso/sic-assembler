package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.HashTable
import so.roth.cop3404.assembler.hash.Keyed
import java.io.File

/**
 * Project 3 - Pass 1
 *
 * @author Rothanak So
 */
typealias SicopsTable = HashTable<Sicop>

data class Sicop(
    val mnemonic: String,
    val opcode: String,
    val format: Int
) : Keyed {
  override fun key(): String = mnemonic
  override fun toString(): String = "$mnemonic\t$opcode\t$format"
}

fun main(args: Array<String>) {
  val inputFile = File("src/main/resources/SICOPS.txt")
  val inputLines = inputFile.readLines().filter { it.isNotBlank() }
  val symbolTable = SicopsTable(inputLines.count())

  // Lines must describe a mnemonic and not a register
  val operation = Regex("^([+*]?[A-Z]+)\\s*([0-9A-F]{2})\\s*(\\d)\\s*\\d\$")

  inputLines.forEach { line ->
    operation.matchEntire(line)?.destructured?.let { (mnemonic, opcode, format) ->
      val sicop = Sicop(mnemonic, opcode, format.toInt())
      symbolTable.insert(sicop)
    }
  }
}
