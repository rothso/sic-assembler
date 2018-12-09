package so.roth.cop3404.assembler

sealed class AssemblyException(message: String) : IllegalStateException(message)

class InvalidMnemonicException(mnemonic: String)
  : AssemblyException("Ignoring invalid mnemonic \"$mnemonic\".")

class InvalidOperandException(operand: String)
  : AssemblyException("Ignoring invalid operand \"$operand\".")

class InvalidRegisterException(register: String)
  : AssemblyException("Ignoring invalid reference to register \"$register\".")

class DuplicateLabelException(label: String)
  : AssemblyException("Duplicate Label \"$label\".")

class MissingLabelException(address: Int, block: String)
  : AssemblyException("Missing label at address ${address.toHex()}" +
    " in block \"$block\".")

class UnknownLabelException(label: String, address: Int)
  : AssemblyException("Unable to resolve label \"$label\" at address ${address.toHex()}")

class BadOperandException(operand: String, address: Int)
  : AssemblyException("Encountered bad operand \"$operand\" at address ${address.toHex()}")

class NoBaseException
  : AssemblyException("Displacement is out of PC-range but there is no BASE")

private fun Int.toHex(): String = this.toString(16).toUpperCase()