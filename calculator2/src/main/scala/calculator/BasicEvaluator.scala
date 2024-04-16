package calculator

class BasicEvaluator extends Evaluator[BasicExpr, BasicEvaluator.EvalResult]:
  import BasicExpr.*
  import BasicEvaluator.*
  import EvalResult.*

  /** Evaluate an expression to its value. */
  def evaluate(e: BasicExpr): EvalResult =
    Ok(42)

object BasicEvaluator:
  enum EvalResult:
    case Ok(v: Double)
    case DivByZero

    def get: Double = this match
      case Ok(v)     => v
      case DivByZero => throw new RuntimeException(s"division by zero")
