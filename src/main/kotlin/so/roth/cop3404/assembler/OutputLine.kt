package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.Line

// Pass 2 artifacts
sealed class OutputLine {
  abstract val number: Int
}

data class CommentOutput(
    override val number: Int,
    val comment: String
) : OutputLine() {
  override fun toString(): String {
    return String.format("%03d- %24s%s", number, "", comment)
  }
}

data class AssembledLine(
    override val number: Int,
    val address: Int,
    val obj: ObjectCode?,
    val line: Line
) : OutputLine() {
  override fun toString(): String {
    return String.format("%03d- %05X %-17s %s", number, address, obj ?: "", line)
  }
}