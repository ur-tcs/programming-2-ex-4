package calculator

import Pos.WithPos

trait Expr extends WithPos

enum TinyExpr extends Expr:
  case Number(value: Double)
  case Add(e1: TinyExpr, e2: TinyExpr)
  case Minus(e1: TinyExpr, e2: TinyExpr)
  case Mul(e1: TinyExpr, e2: TinyExpr)
  case Neg(e: TinyExpr)

  def embed: FullExpr = this match
    case Number(value) => FullExpr.Number(value)
    case Add(e1, e2)   => FullExpr.Add(e1.embed, e2.embed)
    case Minus(e1, e2) => FullExpr.Minus(e1.embed, e2.embed)
    case Mul(e1, e2)   => FullExpr.Mul(e1.embed, e2.embed)
    case Neg(e)        => FullExpr.Neg(e.embed)

enum BasicExpr extends Expr:
  case Number(value: Double)
  case Add(e1: BasicExpr, e2: BasicExpr)
  case Minus(e1: BasicExpr, e2: BasicExpr)
  case Mul(e1: BasicExpr, e2: BasicExpr)
  case Div(e1: BasicExpr, e2: BasicExpr) /* new case */
  case Neg(e: BasicExpr)

enum FullExpr extends Expr:
  case Number(value: Double)
  case Add(e1: FullExpr, e2: FullExpr)
  case Minus(e1: FullExpr, e2: FullExpr)
  case Mul(e1: FullExpr, e2: FullExpr)
  case Div(e1: FullExpr, e2: FullExpr)
  case Neg(e: FullExpr)
  case Var(name: String) /* new case */
