package so.roth.cop3404.assembler

sealed class ObjectCode

data class WordConstant(val value: Int) : ObjectCode() {
  override fun toString(): String {
    return String.format("%06X", value and 0xFFFFFF)
  }
}

data class CharConstant(val chars: String) : ObjectCode() {
  override fun toString(): String {
    return chars.map { it.toInt() }.joinToString("") { String.format("%02X", it) }
  }
}

data class HexConstant(val hex: String) : ObjectCode() {
  override fun toString() = hex
}

data class Format2(val opcode: String, val r1: Int, val r2: Int) : ObjectCode() {
  override fun toString(): String {
    return String.format("%s%X%X", opcode, r1, r2)
  }
}

data class Format3(val opcode: Int, val flag: Int, val targetAddress: Int) : ObjectCode() {
  override fun toString(): String {
    return String.format("%02X%X%03X", opcode, flag, targetAddress and 0xFFF)
  }
}

data class Format4(val opcode: Int, val flag: Int, val targetAddress: Int) : ObjectCode() {
  override fun toString(): String {
    return String.format("%02X%X%05X", opcode, flag, targetAddress and 0xFFFFF)
  }
}