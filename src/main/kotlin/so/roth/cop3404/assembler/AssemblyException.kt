package so.roth.cop3404.assembler

sealed class AssemblyException(message: String) : IllegalStateException(message)

class InvalidMnemonicException(mnemonic: String)
  : AssemblyException("Ignoring Invalid Mnemonic \"$mnemonic\".")

class DuplicateLabelException(label: String)
  : AssemblyException("Duplicate Label \"$label\".")

class MissingLabelException(address: Int, block: String)
  : AssemblyException("Undefined Label At Address ${address.toString(16).toUpperCase()}" +
    " in block \"$block\".")