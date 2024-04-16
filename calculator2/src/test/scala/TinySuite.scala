import calculator.*

trait TinySuite extends munit.FunSuite:
  private def evaluate(source: String): Double =
    TinyDriver.evaluate(source).get

  private val tests = List[(String, Double)](
    "0" -> 0,
    "-1" -> -1,
    "100" -> 100,
    "0+1" -> 1,
    "1-2" -> -1,
    "0*1" -> 0,
    "-1+1" -> 0,
    "1+2-3" -> 0,
    "1-2-3+2+3" -> 1,
    "1*2+3*4-10" -> 4
  )

  for (i, o) <- tests do
    test(s"tiny evaluator: $i should be $o (1pt)") {
      assertEqualsDouble(evaluate(i), o, DELTA)
    }
