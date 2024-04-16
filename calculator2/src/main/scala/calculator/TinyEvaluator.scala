package calculator

class TinyEvaluator extends Evaluator[TinyExpr, Double]:
  import TinyExpr.*

  /** Evaluate an expression to its value. */
  def evaluate(e: TinyExpr): Double =
    42
