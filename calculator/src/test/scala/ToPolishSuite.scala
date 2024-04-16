import calculator.*

trait ToPolishSuite extends munit.FunSuite:
  private val tests = List[(String, String)](
    "0" -> "0.0",
    "1" -> "1.0",
    "1.5" -> "1.5",
    "1+1" -> "+ 1.0 1.0",
    "1-1" -> "- 1.0 1.0",
    "1*1" -> "* 1.0 1.0",
    "1+(2+3)" -> "+ 1.0 + 2.0 3.0",
    "-(1+1)" -> "-- + 1.0 1.0",
    "-(1+2*3*(4-5))" -> "-- + 1.0 * * 2.0 3.0 - 4.0 5.0",
    "1 + -1" -> "+ 1.0 -- 1.0"
  )

  for (i, o) <- tests do
    val e = TinyDriver.parse(i).get
    test(s"toPolish: toString($i) should be ${o} (0.5pt)") {
      assertEquals(TinyPrinters.toPolish(e), o)
    }
