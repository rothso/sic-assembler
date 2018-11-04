package so.roth.cop3404.assembler.hash

import so.roth.cop3404.assembler.util.nextPrime

class HashTable<T : Keyed> constructor(
    numItems: Int,
    private val ps: ProbingStrategy = ProbingStrategy.QUADRATIC
) : Iterable<Node<T>> {
  private val capacity = nextPrime(numItems * 2)
  private val array = MutableList<Node<T>>(capacity) { Node.Uninitialized }

  /**
   * Insert an item into the hash table. Duplicate items will not be inserted.
   */
  fun insert(item: T) {
    var hash = hash(item.key())
    var n = 1

    while (true) {
      val node = array[hash]
      when (node) {
        is Node.Uninitialized, Node.Deleted -> {
          array[hash] = Node.Item(item)
          return
        }
        is Node.Item -> {
          if (node.item.key() == item.key()) return
          hash = ps.nextHash(hash, n++, capacity)
        }
      }
    }
  }

  /**
   * Retrieve an item from the hash table, or null if it does not exist.
   */
  fun find(key: String): T? = search(key) { node, pos -> node.item }

  /**
   * Delete an item from the hash table, returning it if successful.
   */
  fun delete(key: String): T? {
    return search(key) { node, pos ->
      array[pos] = Node.Deleted
      node.item
    }
  }

  private fun search(key: String, f: (node: Node.Item<T>, pos: Int) -> T): T? {
    var hash = hash(key)
    var n = 1

    // Probe for the key
    while (array[hash] != Node.Uninitialized) {
      val node = array[hash]
      if (node is Node.Item<T> && node.item.key() == key)
        return f(node, hash)
      hash = ps.nextHash(hash, n++, capacity)
    }

    // Key not found
    return null
  }

  private fun hash(key: String, acc: Int = 0): Int =
      if (key == "") acc else hash(key.substring(1), (acc * 26 + key[0].toInt()) % capacity)

  override fun iterator(): Iterator<Node<T>> = array.iterator()
}