import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import so.roth.cop3404.assembler.main
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class MainTest {
  private val output = ByteArrayOutputStream()

  @BeforeEach
  fun setUp() = System.setOut(PrintStream(output))

  @AfterEach
  fun tearDown() = System.setOut(System.out)

  @Test
  fun testCorrectOutput() {
    // TODO verify output
    main(arrayOf("src/test/resources/input.txt"))
  }
}