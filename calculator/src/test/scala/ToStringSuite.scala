import calculator.*

trait ToStringSuite extends munit.FunSuite:
  private val tests = List[String](
    "0",
    "1",
    "1.5",
    "1+1",
    "1-1",
    "1*1",
    "1+(2+3)",
    "-(1+1)",
    "-(1+2*3*(4-5))"
  )

  for i <- tests do
    val e = TinyDriver.parse(i).get
    test(s"show: show($i) should be ${e.toString} (0pt)") {
      assertEquals(TinyPrinters.show(e), e.toString)
    }
