package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.grammar.Line

// Pass 1 artifacts
sealed class SourceLine {
  abstract val number: Int
}

data class CommentLine(
    override val number: Int,
    val comment: String
) : SourceLine()

data class AddressedLine(
    override val number: Int,
    val address: Address,
    val line: Line
) : SourceLine()