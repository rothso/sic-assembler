package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.*

class ObjectCodeAssembler(private val addresses: SymbolTable.Absolute) {
  private var base: Int? = null

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
      "NOBASE" -> base = null
    }
  }

  fun onInstruction(instruction: Instruction, currentAddress: Int): ObjectCode? {
    val operand = instruction.operand
    return when (val op = instruction.operation) {
      is UnsupportedOp -> throw UnsupportedOpcodeException(op.operation)
      is DataOp -> when (op.name) {
        "WORD" -> when(operand) {
          is NumericOperand -> WordConstant(operand.value)
          else -> throw MisusedOperandException(operand.toString())
        }
        "BYTE" -> when(operand) {
          is CharOperand -> CharConstant(operand.string)
          is HexOperand -> HexConstant(operand.hexString)
          else -> throw MisusedOperandException(op.toString())
        }
        "RESW" -> null
        "RESB" -> null
        else -> null
      }
      is SicOp -> when (val format = op.format) {
        2 -> {
          when (val ro = instruction.operand) {
            is Register1Operand -> Format2(op.opcode, ro.r1.number, 0)
            is Register2Operand -> Format2(op.opcode, ro.r1.number, ro.r2.number)
            is RegisterNOperand -> Format2(op.opcode, ro.r1.number, ro.n - 1)
            else -> throw MisusedOperandException(ro.toString())
          }
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
          val targetAddress = when (operand) {
            is BlankOperand -> 0
            is NumericOperand -> operand.value
            is HexOperand -> operand.hexString.toInt(16)
            is LabelOperand -> {
              val targetAddress = addresses.getAddress(operand.label)
                  ?: throw UnresolvableLabelException(operand.label)

              when (format) {
                3 -> {
                  val displacement = targetAddress - (currentAddress + op.format)
                  if (displacement >= -2048 && displacement <= 2047) {
                    taFlag = taFlag or Flag.PCREL.flag
                    displacement
                  } else {
                    taFlag = taFlag or Flag.BASEREL.flag
                    targetAddress - (base ?: throw NoBaseException())
                  }
                }
                4 -> {
                  taFlag = taFlag or Flag.EXTENDED.flag
                  targetAddress
                }
                else -> throw IllegalStateException("Bad format") // should never reach here
              }
            }
            else -> throw MisusedOperandException(operand.toString())
          }

          // Return the object code object
          when (op.format) {
            3 -> Format3(opcode, taFlag, targetAddress)
            4 -> Format4(opcode, taFlag, targetAddress)
            else -> throw IllegalStateException("Bad format") // should never reach here
          }
        }
        else -> null
      }
    }
  }
}