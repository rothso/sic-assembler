package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.*

class AddressAssigner {
  private val useRegistry = hashMapOf<String, Block>() // (name, blockNum)
  private val useLocations = arrayListOf<Int>() // last address of each block
  private var block = Block(0, "main") // default block
  private var startAddr = 0
  private var locCounter = 0

  init {
    useLocations.add(0)
  }

  val useStartAddresses
    get() = useLocations.mapIndexed { i, n ->
      n + if (i != 0) useLocations[i - 1] else 0
    }

  fun onDirective(directive: Directive): Address {
    when (directive.name) {
      "START" -> {
        startAddr = directive.operand.toInt(16)
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

