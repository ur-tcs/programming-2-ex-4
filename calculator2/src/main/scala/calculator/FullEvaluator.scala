package calculator

import scala.util.{Try, Success, Failure}

object FullEvaluator:
  /** Result of evaluation. */
  enum FullEvalResult:
    case Ok(v: Double)
    case DivByZero
    case UndefinedVar(name: String)

    def get: Double = this match
      case Ok(v)              => v
      case DivByZero          => throw new RuntimeException("division by zero")
      case UndefinedVar(name) => throw new RuntimeException(s"undefined variable: $name")

  // Define your own context here

  type Ctx =
    Unit

  def empty: Ctx =
    ()

  def cons(name: String, value: Double, tail: Ctx) =
    ()

  def fromList(xs: List[(String, Double)]): Ctx =
    xs match
      case Nil           => empty
      case (n, v) :: rem => cons(n, v, fromList(rem))

class FullEvaluator(ctx: FullEvaluator.Ctx) extends Evaluator[FullExpr, FullEvaluator.FullEvalResult]:
  import FullEvaluator.*
  import FullExpr.*
  import FullEvalResult.*

  /** Evaluate an expression to its value. */
  def evaluate(e: FullExpr): FullEvalResult =
    Ok(42)
