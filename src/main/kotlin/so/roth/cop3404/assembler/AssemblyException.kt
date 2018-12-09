package so.roth.cop3404.assembler

sealed class AssemblyException(message: String) : IllegalStateException(message)

class UnsupportedOpcodeException(mnemonic: String)
  : AssemblyException("Unsupported opcode \"$mnemonic\" found")

class MisusedOperandException(operand: String)
  : AssemblyException("Malformed or improper use of operand \"$operand\" found")

class InvalidRegisterException(register: String)
  : AssemblyException("Ignoring invalid reference to register \"$register\".")

class DuplicateLabelException(label: String)
  : AssemblyException("Duplicate label \"$label\" found")

class MissingLabelException(address: Int, block: String)
  : AssemblyException("Missing label at address ${address.toHex()}" +
    " in block \"$block\".")

class UnresolvableLabelException(label: String)
  : AssemblyException("Operand \"$label\" not found in symbol table")

class NoBaseException
  : AssemblyException("Displacement is out of PC-range but there is no BASE")

private fun Int.toHex(): String = this.toString(16).toUpperCase()