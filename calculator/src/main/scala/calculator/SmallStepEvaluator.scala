package calculator

object SmallStepEvaluator:
  import TinyExpr.*

  /** Evaluate the expression by one step. Return the expression as it is if it
    * has been fully evaluated.
    */
  def step(e: TinyExpr): TinyExpr =
    e
