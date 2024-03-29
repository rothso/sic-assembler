SIC Assembler
===========================

> 2-Pass Assembler with support for USE and Literals


## Purpose

The purpose of this project is to implement both pass 1 and pass 2 of the SIC assembler.
The program uses a hash table to maintain a list of valid mnemonics and a separate hash
table to store labeled instructions with their computed addresses. The program parses
instructions based off a grammar in order to differentiate between labeled and unlabeled
instructions, and directives and instructions, as well as different types of operands.
Pass 1 iterates through the file and transforms it into an intermediate format that is
then iterated through by Pass 2.


This assembler supports the following assembler features:
- BASE (input2.txt and input3.txt)
- USE blocks (input5.txt)
- Literals/LTORG (input6.txt)


## Source files

Source files are located at `src/main/kotlin/so/roth/cop3404/assembler`. The important
files and directories are:

    grammar\                 Contains data classes that model the SIC instructions
    AddressAssigner.kt       Contains pass 1 logic
    ObjectCodeAssembler.kt   Performs pass 2 logic
    Main.kt			 Main method

## Input

The program requires an input file to be passed in as the first argument. The
input file is the SIC program that is to be assembled. The input program will
need to adhere to the following format:

    Col 1-8    Label (optional)
    Col 9      Blank
    Col 10     + or * (optional)
    Col 11-17  Mnemonic
    Col 18     Blank
    Col 19     #, @, = (optional)
    Col 20-29  Operand
    Col 30-31  Blank
    Col 32-80  Comments (optional)

The test files for the program are located at `src/test/resources/input.txt`.

    src/test/resources/input1.txt - Error handling
    src/test/resources/input2.txt - General purpose testing
    src/test/resources/input3.txt - General purpose testing
    src/test/resources/input4.txt - SIC loader source code
    src/test/resources/input5.txt - USE blocks
    src/test/resources/input6.txt - Literals / LTORG


## Output

The program will output two files: pgm.lst and pgm.asm. If there were any assembler
errors, they will be reported in the .lst file and a message will be displayed in the
console.

The following errors will also be reported:

  - Instructions without labels
  - Invalid mnemonics
  - Invalid operands
  - Duplicate labels
  - Unresolvable label operands
  - Missing BASE

There are sample corresponding output .lst and .obj files for each of the
six input files in the `src/test/resources/` directory.


## How to execute

The project was developed using Gradle, but can be built with the Kotlin
compiler directly. The compiler is automatically installed locally during
the first `make`, which will output a JAR that can be executed on the JVM.

  1. Run the `make` command to produce the JAR.
  2. Run `java -jar assembler.jar`, specifying the input file name
  3. Optionally, run `make clean` afterward to free up disk space
