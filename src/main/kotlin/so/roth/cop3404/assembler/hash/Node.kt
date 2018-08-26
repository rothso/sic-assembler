package so.roth.cop3404.assembler.hash

sealed class Node<out T : Keyed> {
  data class Item<out T : Keyed>(val item: T) : Node<T>()
  object Uninitialized : Node<Nothing>()
  object Deleted : Node<Nothing>()
}