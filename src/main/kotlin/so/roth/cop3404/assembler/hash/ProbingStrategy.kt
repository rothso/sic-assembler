package so.roth.cop3404.assembler.hash

interface ProbingStrategy {
  fun nextHash(prevHash: Int, n: Int, max: Int): Int

  companion object {
    val LINEAR = object : ProbingStrategy {
      override fun nextHash(prevHash: Int, n: Int, max: Int): Int =
          (prevHash + 1) % max
    }

    val QUADRATIC = object : ProbingStrategy {
      override fun nextHash(prevHash: Int, n: Int, max: Int): Int =
          (prevHash + 2 * (n - 1) + 1) % max
    }
  }
}