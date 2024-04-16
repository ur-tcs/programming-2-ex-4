package calculator

object Simplifier:
  import FullExpr.*

  /** Fold constant sub-expressions in values. */
  def constfold(e: FullExpr): FullExpr =
    e

  // simplification rules
  // 1. 0 + e = e + 0 = e
  // 2. 0 - e = -e
  // 3. e - 0 = e
  // 4. 0 * e = e * 0 = 0
  // 5. 1 * e = e * 1 = e
  // 6. e / 1 = e
  // 7. e - e = 0
  /** Simplifiy expressions based on the listed algebraic rules. */
  def algebraic(e: FullExpr): FullExpr =
    e

  def simplify(e: FullExpr): FullExpr =
    e
