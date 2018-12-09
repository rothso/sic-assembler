package so.roth.cop3404.assembler

data class Block(val number: Int, val name: String)

data class Address(val relative: Int, val block: Block)
