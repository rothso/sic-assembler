package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.util.sizeBytes

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
    locCounter += when (val operation = instruction.operation) {
      is DataOp -> when (operation.name) {
        // TODO avoid the need to do hacky casts
        "WORD" -> 3
        "RESW" -> 3 * (instruction.operand as NumberOperand).value.toString(16).toInt()
        "BYTE" -> 1 * sizeBytes((instruction.operand as LabelOperand).label)
        "RESB" -> (instruction.operand as NumberOperand).value
        else -> throw InvalidMnemonicException(operation.name)
      }
      is SicOp -> operation.format
    }
    return address // relative address
  }
}

