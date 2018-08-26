import org.junit.jupiter.api.Test
import so.roth.cop3404.assembler.main

class MainTest {
  //  private val output = ByteArrayOutputStream()

  //  @BeforeEach
  //  fun setUp() = System.setOut(PrintStream(output))
  //
  //  @AfterEach
  //  fun tearDown() = System.setOut(System.out)

  @Test
  fun testCorrectOutput() {
    // TODO verify output
    main(arrayOf("src/test/resources/input.txt"))
  }
}