package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.HashTable
import so.roth.cop3404.assembler.hash.Keyed
import so.roth.cop3404.assembler.hash.Node

class SymbolTable(private val maxSize: Int) {
  private val symbolTable = HashTable<RelativeLine>(maxSize)

  private data class RelativeLine(val address: Address, val line: Line) : Keyed by line
  private data class AbsoluteLine(val address: Int, val line: Line) : Keyed by line

  inner class Absolute(private val useOffsets: List<Int>) {
    private val absoluteTable = HashTable<AbsoluteLine>(maxSize)

    fun getAddress(label: String): Int? {
      return absoluteTable.find(label)?.address ?: findAndInsertAddress(label)
    }

    private fun findAndInsertAddress(label: String): Int? {
      val (address, line) = symbolTable.find(label) ?: return null
      return insertAddress(address, line)
    }

    fun insertAddress(address: Address, line: Line): Int {
      val absoluteAddress = address.relative + useOffsets[address.block.number]
      line.label?.let { absoluteTable.insert(AbsoluteLine(absoluteAddress, line)) }
      return absoluteAddress
    }
  }

  fun store(address: Address, line: Line) {
    // Fail if there is no label
    if (line.label == null)
      throw MissingLabelException(address.relative, address.block.name)

    // Fail of the label already exists
    if (symbolTable.find(line.key()) != null)
      throw DuplicateLabelException(line.label)

    symbolTable.insert(RelativeLine(address, line))
  }

  fun toAbsolute(useOffsets: List<Int>): SymbolTable.Absolute {
    return Absolute(useOffsets)
  }

  fun printTable() {
    val headerF = "%-15s %-10s %-10s %-10s\n"
    val rowF = "%-15d %-10s %04X       %-10s\n"

    // Print the header row
    System.out.printf(headerF, "Table Location", "Label", "Address", "Use")

    // Print the indices alongside the stored items
    symbolTable
        .withIndex()
        .filter { it.value is Node.Item<RelativeLine> }
        .filterIsInstance<IndexedValue<Node.Item<RelativeLine>>>()
        .map { (i, node) -> Triple(i, node.item.address, node.item.line) }
        .forEach { (i, a, l) -> System.out.printf(rowF, i, l.label, a.relative, a.block.name) }
  }
}