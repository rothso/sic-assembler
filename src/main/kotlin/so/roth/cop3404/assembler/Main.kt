package so.roth.cop3404.assembler

import so.roth.cop3404.assembler.hash.HashTable
import java.io.File

/**
 * Project 1 - Symbol Table
 *
 * For this project you are to implement the symbol table that will be used by the assembler
 * during pass 1 and pass 2. It should be constructed as an efficient hashing table. You should
 * construct a "main" routine that will invoke the symbol table operations. The main routine
 * should read a file name off the command line.
 *
 * @author Rothanak So
 */
typealias SymbolTable = HashTable<Symbol>

fun main(args: Array<String>) {
  val inputFile = File(args[0])
  val symbolTable = SymbolTable(inputFile.readLines().count())

  // Lines must contain an alphabetic key and optional number
  val regexKeyAndNumber = Regex("^[\\s]*([a-zA-Z]+) ([0-9]+)$")
  val regexKeyOnly = Regex("^[\\s]*[a-zA-Z]+$")

  inputFile.forEachLine { line ->
    when (line) {
      in regexKeyAndNumber -> {
        // Store the key-value pair in the table
        val (name, number) = regexKeyAndNumber.matchEntire(line)!!.destructured
        symbolTable.insert(Symbol(name, number.toInt()))
      }
      in regexKeyOnly -> symbolTable.find(line)
      else -> println("Unrecognized input: $line")
    }
  }
}

// https://discuss.kotlinlang.org/t/using-regex-in-a-when/1794/6
operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)