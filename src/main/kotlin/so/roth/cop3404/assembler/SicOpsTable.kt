package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.Register
import so.roth.cop3404.assembler.grammar.SicOp
import so.roth.cop3404.assembler.hash.HashTable
import java.io.File

class SicOpsTable(sicOpsTxt: String = "src/main/resources/SICOPS.txt") {
  private val sicOpsTable: HashTable<SicOp>
  private val registerTable: HashTable<Register>

  init {
    val sicOpsFile = File(sicOpsTxt)
    val inputLines = sicOpsFile.readLines().filter { it.isNotBlank() }
    val numLines = inputLines.count()

    // Lines must describe a mnemonic or a register
    val operationDef = Regex("^([+*]?[A-Z]+)\\s*([0-9A-F]{2})\\s*(\\d)\\s*\\d\$")
    val registerDef = Regex("^([A-Z]+)\\s*(\\d)\$")

    // Read and store sicOps information
    sicOpsTable = HashTable(numLines)
    registerTable = HashTable(10)
    inputLines.forEach { line ->
      operationDef.matchEntire(line)?.destructured?.let { (mnemonic, opcode, format) ->
        val sicOp = SicOp(mnemonic, opcode, format.toInt())
        sicOpsTable.insert(sicOp)
      }

      registerDef.matchEntire(line)?.destructured?.let { (name, number) ->
        val register = Register(name, number.toInt())
        registerTable.insert(register)
      }
    }
  }

  fun getSicOp(mnemonic: String, modifier: String? = null) : SicOp? {
    return sicOpsTable.find((modifier ?: "") + mnemonic)
  }

  fun getRegister(name: String) : Register? {
    return registerTable.find(name)
  }
}