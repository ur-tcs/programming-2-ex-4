import calculator.*

trait BasicSuite extends munit.FunSuite:
  import BasicEvaluator.*

  private def evaluate(source: String): EvalResult =
    BasicDriver.evaluate(source).get

  private val okTests = List[(String, Double)](
    "0" -> 0,
    "-1" -> -1,
    "100" -> 100,
    "0+1" -> 1,
    "1-2" -> -1,
    "0*1" -> 0,
    "1/2" -> 0.5,
    "-1+1" -> 0,
    "-1/(1+1)" -> -0.5,
    "-(-2*2)/(1/5)-19" -> 1,
    "2*(3+4/2)" -> 10,
    "1/0.1" -> 10,
    "1/10+1" -> 1.1,
    "1+2-3" -> 0,
    "1-2-3+2+3" -> 1
  )

  private val badTests = List[String](
    "0/0",
    "1/0",
    "1/(1-1)",
    "1/(0*1)",
    "1+3/(2*0)"
  )

  for (i, o) <- okTests do
    test(s"basic evaluator: $i should be Ok($o) (0.4pt)") {
      assertEqualsDouble(evaluate(i).get, o, DELTA)
    }

  for s <- badTests do
    test(s"basic evaluator: $s should result in an error (0.8pt)") {
      assertEquals(evaluate(s), EvalResult.DivByZero)
    }
