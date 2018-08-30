
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import so.roth.cop3404.assembler.main
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class MainTest {
  companion object {
    private const val RESOURCE_DIR = "src/test/resources/"
  }

  private val output = ByteArrayOutputStream()

  @BeforeEach
  fun setUp() = System.setOut(PrintStream(output))

  @AfterEach
  fun tearDown() = System.setOut(System.out)

  @Test
  fun testCorrectOutput() {
    // Run the program
    main(arrayOf("${RESOURCE_DIR}input.txt"))

    // Ensure the console output is correct
    val output = output.toString()
    val expected = File("${RESOURCE_DIR}output.txt").readText()
    assertEquals(expected, output)
  }
}