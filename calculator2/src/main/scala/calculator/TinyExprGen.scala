package calculator

import util.Random

class TinyExprGen(using rnd: Random):
  sealed trait Expr
  case class Number(v: Double) extends Expr
  case class Add(e1: Expr, e2: Expr) extends Expr
  case class Minus(e1: Expr, e2: Expr) extends Expr
  case class Mul(e1: Expr, e2: Expr) extends Expr
  case class Neg(e: Expr) extends Expr
  case class ExprVar() extends Expr:
    val id = ExprVar.nextId
    var myInstance: Expr | Null = null

    def instantiated: Boolean = myInstance ne null
    def instance: Expr = myInstance.nn

    def instantiate(e: Expr): this.type =
      myInstance = e
      this

    override def toString(): String =
      if instantiated then s"[$instance]"
      else s"[???@$id]"

  object ExprVar:
    private var myCount: Int = 0
    def nextId: Int =
      myCount += 1
      myCount

  def genNumber: (Number, ValueFunction) =
    val v = rnd.between(-100, 100)
    (Number(v), ValueFunction.constant(v))

  extension (e: Expr)
    def depth: Int = e match
      case Number(v)     => 1
      case Add(e1, e2)   => 1 + (e1.depth max e2.depth)
      case Minus(e1, e2) => 1 + (e1.depth max e2.depth)
      case Mul(e1, e2)   => 1 + (e1.depth max e2.depth)
      case Neg(e)        => 1 + e.depth
      case e: ExprVar    => if e.instantiated then e.instance.depth else 1

    def compile: TinyExpr = e match
      case Number(v)     => TinyExpr.Number(v)
      case Add(e1, e2)   => TinyExpr.Add(e1.compile, e2.compile)
      case Minus(e1, e2) => TinyExpr.Minus(e1.compile, e2.compile)
      case Mul(e1, e2)   => TinyExpr.Mul(e1.compile, e2.compile)
      case Neg(e)        => TinyExpr.Neg(e.compile)
      case e: ExprVar    => e.instance.compile

    def holes: List[ExprVar] = e match
      case Number(v)     => Nil
      case Add(e1, e2)   => e1.holes ++ e2.holes
      case Minus(e1, e2) => e1.holes ++ e2.holes
      case Mul(e1, e2)   => e1.holes ++ e2.holes
      case Neg(e)        => e.holes
      case e: ExprVar    => if e.instantiated then e.instance.holes else List(e)

  case class ValueFunction(domain: Set[Int], compute: Map[Int, Double] => Double):
    def composedWith(other: ValueFunction, at: Int): ValueFunction =
      assert(domain.contains(at))
      assert(other.domain.intersect(domain).isEmpty)
      val newDomain = (domain - at) ++ other.domain
      val newCompute = (m: Map[Int, Double]) =>
        val t = other.compute(m)
        compute(m + (at -> t))
      ValueFunction(newDomain, newCompute)
    override def toString: String = s"{${domain.map(_.toString).mkString(",")}} ==> Double"

  object ValueFunction:
    def constant(v: Double): ValueFunction = ValueFunction(Set.empty, _ => v)

    def binOp(e1: ExprVar, e2: ExprVar, op: (Double, Double) => Double) =
      ValueFunction(Set(e1.id, e2.id), m => op(m(e1.id), m(e2.id)))

    def unaryOp(e1: ExprVar, op: Double => Double) =
      ValueFunction(Set(e1.id), m => op(m(e1.id)))

  case class ExprConfig(tree: Expr, holes: List[ExprVar], valueFunc: ValueFunction):
    assert(holes.length == valueFunc.domain.size)
    def isComplete: Boolean = holes.isEmpty

    def instantiateVar(ev: ExprVar, expr: Expr, func: ValueFunction): ExprConfig =
      ev.instantiate(expr)
      val func1 = valueFunc.composedWith(func, at = ev.id)
      val holes1 = holes.filterNot(_.id == ev.id) ++ expr.holes
      ExprConfig(tree, holes1, func1)

    def instantiateAll: ExprConfig =
      var config: ExprConfig = this
      holes.foreach: evar =>
        val (num, f) = genNumber
        config = config.instantiateVar(evar, num, f)
      config

    def compile: TinyExpr = tree.compile

  def genNode(noNum: Boolean = false): (Expr, ValueFunction) = rnd.between(if noNum then 1 else 0, 5) match
    case 0 => genNumber
    case 1 =>
      val e1 = ExprVar()
      val e2 = ExprVar()
      (Add(e1, e2), ValueFunction.binOp(e1, e2, _ + _))
    case 2 =>
      val e1 = ExprVar()
      val e2 = ExprVar()
      (Minus(e1, e2), ValueFunction.binOp(e1, e2, _ - _))
    case 3 =>
      val e1 = ExprVar()
      val e2 = ExprVar()
      (Mul(e1, e2), ValueFunction.binOp(e1, e2, _ * _))
    case 4 =>
      val e1 = ExprVar()
      (Neg(e1), ValueFunction.unaryOp(e1, x => -x))

  def step(config: ExprConfig): ExprConfig =
    val ExprConfig(tree, holes, value) = config
    holes match
      case Nil => config
      case ev :: rem =>
        val (node, f) = genNode(tree.depth == 1)
        config.instantiateVar(ev, node, f)

  def expand(config: ExprConfig, maxDepth: Int): ExprConfig =
    @annotation.tailrec
    def recur(now: ExprConfig): ExprConfig =
      if now.isComplete then now
      else if now.tree.depth >= maxDepth then now.instantiateAll
      else
        val config1 = step(now)
        recur(config1)
    recur(config)

  def generate(maxDepth: Int): (TinyExpr, Double) =
    val initial = ExprConfig.initial
    val config1 = expand(initial, maxDepth)
    (config1.compile, config1.valueFunc.compute(Map.empty))

  object ExprConfig:
    def initial: ExprConfig =
      val ev = ExprVar()
      ExprConfig(ev, ev :: Nil, ValueFunction.unaryOp(ev, x => x))

object TinyExprGen:
  def generate(maxDepth: Int)(using Random): (TinyExpr, Double) =
    val gen = TinyExprGen()
    gen.generate(maxDepth)
