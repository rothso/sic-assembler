package so.roth.cop3404.assembler

class ObjectCodeAssembler(
    private val sicOps: SicOpsTable,
    private val addresses: SymbolTable.Absolute
) {
  var base: Int? = null

  private enum class Flag(val flag: Int) {
    INDIRECT(0b000010),
    IMMEDIATE(0b000001),
    INDEXED(0b001000),
    BASEREL(0b00100),
    PCREL(0b000010),
    EXTENDED(0b000001)
  }

  fun onDirective(directive: Directive) {
    when (directive.name) {
      "BASE" -> base = addresses.getAddress(directive.operand)
    }
  }

  fun onInstruction(instruction: Instruction, currentAddress: Int): ObjectCode? {
    return when (val op = instruction.operation) {
      is DataOp -> when (op.name) {
        "WORD" -> WordConstant((instruction.operand as NumberOperand).value)
        "BYTE" -> null // TODO
        "RESW" -> null
        "RESB" -> null
        else -> null
      }
      is SicOp -> when (val format = op.format) {
        2 -> {
          val (register1, register2) = instruction.operand as RegisterOperand
          // TODO: parse register in Parser
          val r1 = sicOps.getRegister(register1)?.number ?: throw IllegalStateException()
          val r2 = sicOps.getRegister(register2)?.number ?: throw IllegalStateException()
          Format2(op.opcode, r1, r2)
        }
        3, 4 -> {
          // Handle the opcode flags
          val opFlag = when {
            instruction.modifier == "*" -> 0
            instruction.special == "@" -> Flag.INDIRECT.flag
            instruction.special == "#" -> Flag.IMMEDIATE.flag
            else -> Flag.INDIRECT.flag or Flag.IMMEDIATE.flag
          }

          // Add the opflags to the opcode
          val opcode = op.opcode.toInt(16) + opFlag

          // Handle the addressing flags
          var taFlag = if (instruction.operand is IndexedOperand) Flag.INDEXED.flag else 0

          // Handle the target address
          val targetAddress = when (val operand = instruction.operand) {
            is RegisterOperand -> throw IllegalStateException("Cannot use register-to-register")
            is NumberOperand -> operand.value
            is LabelOperand -> {
              val targetAddress = addresses.getAddress(operand.label)
                  ?: throw IllegalStateException("The target label ${operand.label} does not exist")

              when (format) {
                3 -> {
                  val disp = targetAddress - (currentAddress + op.format)
                  if (disp >= -2048 && disp <= 2047) {
                    taFlag = taFlag or Flag.PCREL.flag
                    disp
                  } else {
                    taFlag = taFlag or Flag.BASEREL.flag
                    targetAddress - (base ?: throw IllegalStateException("no base"))
                  }
                }
                4 -> {
                  taFlag = taFlag or Flag.EXTENDED.flag
                  targetAddress
                }
                else -> throw IllegalStateException() // should never reach here
              }
            }
          }

          // Return the object code object
          when (op.format) {
            3 -> Format3(opcode, taFlag, targetAddress)
            4 -> Format4(opcode, taFlag, targetAddress)
            else -> throw IllegalStateException() // should never reach here
          }
        }
        else -> null
      }
    }
  }
}