package so.roth.cop3404.assembler

data class AssemblyError(val message: String) {
  override fun toString(): String = message
}