package calculator

import util.*

import GraphvizPrinter.*
import scala.jdk.CollectionConverters.*

object TinyDriver:
  def parse(source: String): Try[TinyExpr] = Try:
    val tokens = Tokenizer.tokenize(source).toTry.get
    Parsers.TinyParser.parse(tokens).toTry.get

  def evaluate(source: String): Try[Double] = Try:
    val evaluator = new TinyEvaluator
    val expr = parse(source).get
    evaluator.evaluate(expr)

  def reduce(expr: TinyExpr): List[TinyExpr] =
    @annotation.tailrec
    def recur(head: TinyExpr, acc: List[TinyExpr]): List[TinyExpr] =
      val head1 = SmallStepEvaluator.step(head)
      if head1 == head then
        (head :: acc).reverse
      else
        recur(head1, head :: acc)
    recur(expr, Nil)

  def reduce(source: String): Try[List[TinyExpr]] = Try:
    val expr = parse(source).get
    reduce(expr)

  def render(source: String): Try[String] = Try:
    val expr = parse(source).get
    expr.toGraphviz

object BasicDriver:
  def parse(source: String): Try[BasicExpr] = Try:
    val tokens = Tokenizer.tokenize(source).toTry.get
    Parsers.BasicParser.parse(tokens).toTry.get

  def evaluate(source: String): Try[BasicEvaluator.EvalResult] = Try:
    val expr = parse(source).get
    evaluate(expr)

  def evaluate(expr: BasicExpr): BasicEvaluator.EvalResult =
    (new BasicEvaluator).evaluate(expr)

object FullDriver:
  import FullExpr.*
  import FullEvaluator.*
  import Printers.*

  def parse(source: String): Try[FullExpr] = Try:
    val tokens = Tokenizer.tokenize(source).toTry.get
    Parsers.FullParser.parse(tokens).toTry.get

  def parseDef(source: String): Try[(String, FullExpr) | FullExpr] = Try:
    val tokens = Tokenizer.tokenize(source).toTry.get
    val result = Parsers.FullParser.parseDef(tokens)
    result.toTry.get

  def parseAssign(source: String): Try[(String, FullExpr)] = Try:
    val tokens = Tokenizer.tokenize(source).toTry.get
    val result = Parsers.FullParser.parseAssign(tokens)
    result.toTry.get

  case class Block(defs: List[(String, FullExpr)], expr: FullExpr)
  def parseBlock(source: String): Try[Block] = Try:
    def recur(lines: List[String], acc: List[(String, FullExpr)]): Block =
      lines match
        case x1 :: xs =>
          xs match
            case Nil =>
              val e = parse(x1).get
              Block(acc.reverse, e)
            case xs =>
              val d = parseAssign(x1).get
              recur(xs, d :: acc)
        case Nil => throw RuntimeException("empty input")
    val lines = source.linesIterator.toList
    recur(lines, Nil)

  case class EvaluatedBlock(defs: List[(String, Double)], expr: FullEvalResult)
  def evaluateBlock(block: Block): Try[EvaluatedBlock] = Try:
    import FullEvaluator.*
    var curCtx: Ctx = empty
    var curAcc: List[(String, Double)] = Nil
    block.defs.foreach: (name, expr) =>
      val res = evaluate(expr, curCtx).get match
        case FullEvalResult.Ok(v)     => v
        case FullEvalResult.DivByZero => throw RuntimeException(s"error when evaluating ${expr.show}: division by zero")
        case FullEvalResult.UndefinedVar(name) =>
          throw RuntimeException(s"error when evaluating ${expr.show}: undefined var $name")
      curCtx = cons(name, res, curCtx)
      curAcc = (name -> res) :: curAcc
    EvaluatedBlock(curAcc.reverse, evaluate(block.expr, curCtx).get)

  def evaluate(source: String, ctx: FullEvaluator.Ctx): Try[FullEvalResult] = Try:
    val evaluator = new FullEvaluator(ctx)
    val expr = parse(source).get
    evaluator.evaluate(expr)

  def evaluate(e: FullExpr, ctx: FullEvaluator.Ctx): Try[FullEvalResult] = Try:
    val evaluator = new FullEvaluator(ctx)
    evaluator.evaluate(e)

  def simplify(source: String): Try[FullExpr] = Try:
    val expr = parse(source).get
    Simplifier.simplify(expr)

  def constfold(source: String): Try[FullExpr] = Try:
    val expr = parse(source).get
    Simplifier.constfold(expr)

  def algebraic(source: String): Try[FullExpr] = Try:
    val expr = parse(source).get
    Simplifier.algebraic(expr)
