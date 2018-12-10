package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.*

class AddressAssigner {
  private val useRegistry = hashMapOf<String, Block>() // (name, blockNum)
  private val useLocations = arrayListOf<Int>() // last address of each block
  private var block = Block(0, "main") // default block
  private var startAddr = 0
  private var locCounter = 0

  init {
    useRegistry[""] = block
    useLocations.add(0)
  }

  val useStartAddresses
    get() = let {
      // Save the last address
      useLocations[block.number] = locCounter

      // Return a list of the offsets for each block
      mutableListOf<Int>().apply {
        for (i in 0..useLocations.size)
          add(i, if (i != 0) this[i - 1] + useLocations[i - 1] else 0)
      }.toList()
    }

  fun onDirective(directive: Directive): Address {
    when (directive.name) {
      "START" -> {
        startAddr = directive.operand.toIntOrNull(16) ?: 0
        locCounter = startAddr
      }
      "USE" -> {
        // Save the last address
        useLocations[block.number] = locCounter

        // Restore the last address of the new use block
        block = useRegistry.getOrPut(directive.operand) {
          Block(useRegistry.size, directive.operand).also { useLocations.add(0) }
        }
        locCounter = useLocations[block.number]
      }
      "END" -> {
        // Save the last address
        useLocations[block.number] = locCounter

        // END belongs in the last block
        val lastAddress = useLocations.last()
        val lastBlock = useRegistry.maxBy { it.value.number }?.value ?: block
        return Address(lastAddress, lastBlock)
      }
    }
    return Address(locCounter, block) // relative address
  }

  fun onInstruction(instruction: Instruction): Address {
    val address = Address(locCounter, block)
    val operand = instruction.operand
    locCounter += when (val op = instruction.operation) {
      is DataOp -> when (op.name) {
        "WORD" -> 3
        "BYTE" -> when (operand) {
          is CharOperand -> operand.string.length
          is HexOperand -> operand.hexString.length / 2
          else -> 0
        }
        "RESW" -> when (operand) {
          is NumericOperand -> 3 * operand.value
          else -> 0
        }
        "RESB" -> when (operand) {
          is NumericOperand -> operand.value
          else -> 0
        }
        else -> throw UnsupportedOpcodeException(op.name)
      }
      is SicOp -> op.format
      is UnsupportedOp -> 0
    }
    return address // relative address
  }
}