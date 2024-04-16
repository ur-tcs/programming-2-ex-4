import calculator.*

enum ResultSpec:
  case Ok(v: Double)
  case NotFound
  case DivByZero

trait EvalVarSuite extends munit.FunSuite:
  import FullEvaluator.*
  import ResultSpec.*

  private def evaluate(source: String, ctx: Ctx): FullEvalResult =
    FullDriver.evaluate(source, ctx).get

  case class TestSpec(ctx: Ctx, tests: List[(String, ResultSpec)])

  private val tests: List[TestSpec] = List(
    TestSpec(
      fromList("a" -> 1 :: Nil),
      List(
        "a" -> Ok(1.0),
        "b" -> NotFound,
        "a*a" -> Ok(1.0),
        "-a" -> Ok(-1.0),
        "a + a * a" -> Ok(2.0),
        "2*a + 3*a + a/2" -> Ok(5.5),
        "a+a-2/(2*a)" -> Ok(1),
        "a/0" -> DivByZero,
        "0/0" -> DivByZero,
        "a/(a-1)" -> DivByZero
      )
    ),
    TestSpec(
      fromList("a" -> 1.0 :: "b" -> 0.0 :: Nil),
      List(
        "a" -> Ok(1.0),
        "b" -> Ok(0.0),
        "a - b" -> Ok(1.0),
        "b - a" -> Ok(-1.0),
        "b * (2 + 3 + 4)" -> Ok(0.0),
        "c" -> NotFound,
        "a + b + c" -> NotFound,
        "1/b" -> DivByZero,
        "a + b * 2" -> Ok(1),
        "a / (b - 1 + 1)" -> DivByZero
      )
    )
  )

  def showTest(ctx: Ctx, e: String, res: ResultSpec): String =
    val resStr = res match
      case NotFound  => "throw a variable-not-found error"
      case DivByZero => "throw a division-by-zero error"
      case Ok(v)     => s"return $v"
    s"full evaluator: evaluating $e under $ctx should $resStr (0.5pt)"

  def runTest(ctx: Ctx, e: String, res: ResultSpec): Unit =
    res match
      case Ok(v) => evaluate(e, ctx) match
          case FullEvalResult.Ok(v0) =>
            assertEqualsDouble(v0, v, 0.000001)
          case _ => fail("should succeed")
      case NotFound => evaluate(e, ctx) match
          case FullEvalResult.UndefinedVar(name) =>
          case _                                 => fail("should fail with a variable-not-found error")
      case DivByZero => evaluate(e, ctx) match
          case FullEvalResult.DivByZero =>
          case _                        => fail("should fail with a division-by-zero error")

  for TestSpec(ctx, cases) <- tests do
    for (expr, res) <- cases do
      test(showTest(ctx, expr, res)) {
        runTest(ctx, expr, res)
      }
