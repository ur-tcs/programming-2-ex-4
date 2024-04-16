import calculator.*
import TinyExpr.*
import scala.util.Random

trait SmallStepSuite extends munit.FunSuite:
  import Printers.*

  def isValidStep(e1: TinyExpr, e2: TinyExpr): Unit = (e1, e2) match
    case (Number(v1), Number(v2)) if v1 == v2 =>
    case (Add(Number(v11), Number(v12)), Number(v2)) =>
      assertEqualsDouble(v11 + v12, v2, DELTA)
    case (Add(Number(v11), e12), Add(Number(v21), e22)) =>
      assertEqualsDouble(v11, v21, DELTA)
      isValidStep(e12, e22)
    case (Add(e11, e12), Add(e21, e22)) =>
      assertEquals(
        e12,
        e22,
        s"in ${e1.embed.show} (reduced to ${e2.embed.show}), lhs should be reduced first (into ${e21.embed.show})"
      )
      isValidStep(e11, e21)
    case (Minus(Number(v11), Number(v12)), Number(v2)) =>
      assertEqualsDouble(v11 - v12, v2, DELTA)
    case (Minus(Number(v11), e12), Minus(Number(v21), e22)) =>
      assertEqualsDouble(v11, v21, DELTA)
      isValidStep(e12, e22)
    case (Minus(e11, e12), Minus(e21, e22)) =>
      assertEquals(
        e12,
        e22,
        s"in ${e1.embed.show} (reduced to ${e2.embed.show}), lhs should be reduced first (into ${e21.embed.show})"
      )
      isValidStep(e11, e21)
    case (Mul(Number(v11), Number(v12)), Number(v2)) =>
      assertEqualsDouble(v11 * v12, v2, DELTA)
    case (Mul(Number(v11), e12), Mul(Number(v21), e22)) =>
      assertEqualsDouble(v11, v21, DELTA)
      isValidStep(e12, e22)
    case (Mul(e11, e12), Mul(e21, e22)) =>
      assertEquals(
        e12,
        e22,
        s"in ${e1.embed.show} (reduced to ${e2.embed.show}), lhs should be reduced first (into ${e21.embed.show})"
      )
      isValidStep(e11, e21)
    case (Neg(Number(v1)), Number(v2)) =>
      assertEqualsDouble(-v1, v2, DELTA)
    case (Neg(e1), Neg(e2)) =>
      isValidStep(e1, e2)
    case (e1, e2) => fail(s"invalid step from $e1 to $e2")

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
    "1*2+3*(1-2-3)" -> -10
  )

  tests.foreach: (i, o) =>
    test(s"smallstep: $i should be small-step evaluated to $o (0.5pt)") {
      val steps = TinyDriver.reduce(i).get
      steps.zip(steps.tail).foreach: (e1, e2) =>
        isValidStep(e1, e2)
      steps.last match
        case Number(v0) => assertEqualsDouble(v0, o, DELTA)
        case e          => fail(s"the end result should be a number, but found $e")
    }

  given smallStepRnd: Random = new Random(42)

  for i <- 0 until 100 do
    val (expr, value) = TinyExprGen.generate(maxDepth = 8)
    test(s"smallstep: random test $i: reduce ${expr.embed.show} (0.1pt)") {
      val steps = TinyDriver.reduce(expr)
      steps.zip(steps.tail).foreach: (e1, e2) =>
        isValidStep(e1, e2)
      steps.last match
        case Number(v0) => assertEqualsDouble(v0, value, DELTA)
        case e          => fail(s"the end result should be a number, but found $e")
    }
