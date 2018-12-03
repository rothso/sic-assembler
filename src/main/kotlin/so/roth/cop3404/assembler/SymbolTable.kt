package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.HashTable
import so.roth.cop3404.assembler.hash.Keyed
import so.roth.cop3404.assembler.hash.Node

class SymbolTable(numLines: Int) {
  private val symbolTable = HashTable<AddressedLine>(numLines)

  data class AddressedLine(
      val address: Address,
      val line: Line
  ) : Keyed by line

  fun store(address: Address, line: Line) {
    // Fail if there is no label
    if (line.label == null)
      throw MissingLabelException(address.relative, address.block.name)

    // Fail of the label already exists
    if (symbolTable.find(line.key()) != null)
      throw DuplicateLabelException(line.label)

    symbolTable.insert(AddressedLine(address, line))
  }

  fun printTable() {
    val headerF = "%-15s %-10s %-10s %-10s\n"
    val rowF = "%-15d %-10s %04X       %-10s\n"

    // Print the header row
    System.out.printf(headerF, "Table Location", "Label", "Address", "Use")

    // Print the indices alongside the stored items
    symbolTable
        .withIndex()
        .filter { it.value is Node.Item<AddressedLine> }
        .filterIsInstance<IndexedValue<Node.Item<AddressedLine>>>()
        .map { (i, node) -> Triple(i, node.item.address, node.item.line) }
        .forEach { (i, a, l) -> System.out.printf(rowF, i, l.label, a.relative, a.block.name) }
  }
}