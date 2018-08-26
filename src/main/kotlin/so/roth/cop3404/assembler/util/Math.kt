package so.roth.cop3404.assembler.util

import kotlin.math.sqrt

fun nextPrime(num: Int): Int {
  var x = num
  while (true)
    if ((++x).isPrime())
      return x
}

private fun Int.isPrime(): Boolean {
  for (i in 2..sqrt(this.toDouble()).toInt())
    if (this % i == 0) return false
  return true
}