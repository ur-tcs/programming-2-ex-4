import calculator.*

import FullExpr.*

import scala.util.Random

extension (name: String)
  def asVar: Var = Var(name)

extension (v: Double)
  def asNum: FullExpr = Number(v)

extension (e: FullExpr)
  def neg: FullExpr = Neg(e)
  def +(other: FullExpr): FullExpr = Add(e, other)
  def -(other: FullExpr): FullExpr = Minus(e, other)
  def *(other: FullExpr): FullExpr = Mul(e, other)
  def /(other: FullExpr): FullExpr = Div(e, other)

trait SimpSuite extends munit.FunSuite:
  import Printers.*

  for (i, o) <- SimpSuite.tests do
    test(s"combined simplifier: simplify $i should be ${o.show} (0.1pt)") {
      assertEquals(FullDriver.simplify(i).get, o)
    }

  given simpRnd: Random = new Random(42)

  test("combined simplifier: randomized tests (11.5pt)") {
    (0 until 1000).foreach: idx =>
      val (key, expr) = SimpTestGen.generate(idx + 5)
      val answer = Simplifier.simplify(expr)
      assertEquals(
        answer,
        key,
        s"random test: simplify ${expr.show} should be ${key.show}, but it is actually ${answer.show}"
      )
  }

object SimpSuite:
  val tests: List[(String, FullExpr)] = List(
    "1+1" -> 2.asNum,
    "1+2+3" -> 6.asNum,
    "0+a" -> "a".asVar,
    "0+a/b" -> ("a".asVar / "b".asVar),
    "a+0" -> "a".asVar,
    "(a+b)+0" -> ("a".asVar + "b".asVar),
    "0-a" -> "a".asVar.neg,
    "(1-1)-a" -> "a".asVar.neg,
    "a-0" -> "a".asVar,
    "(a+b)-0" -> ("a".asVar + "b".asVar),
    "(a+b)-(0*a)" -> ("a".asVar + "b".asVar),
    "(a+b)-(1-1+a*0)" -> ("a".asVar + "b".asVar),
    "0*a" -> Number(0),
    "0*(a+2)" -> Number(0),
    "(1-1)*(a+b)" -> Number(0),
    "a*0" -> Number(0),
    "(a+2)*0" -> Number(0),
    "(a+b*c)*(1-1)" -> Number(0),
    "(a+b*c)*(1-1)+1/5" -> Number(0.2),
    "1*a" -> "a".asVar,
    "1*(a+b)" -> ("a".asVar + "b".asVar),
    "(0*a+1-0)*(a+b)" -> ("a".asVar + "b".asVar),
    "a*1" -> "a".asVar,
    "(a+b)*1" -> ("a".asVar + "b".asVar),
    "(a+b)*(1*1+0*a)" -> ("a".asVar + "b".asVar),
    "a/1" -> "a".asVar,
    "(a+b)/1" -> ("a".asVar + "b".asVar),
    "(a+b)/(2/2)" -> ("a".asVar + "b".asVar),
    "a-a" -> 0.asNum,
    "(a+a)-(a+a)" -> 0.asNum,
    "(a-a+1)*a*a" -> ("a".asVar * "a".asVar),
    "(a-a+1)*a*a-a*a" -> 0.asNum,
    "1*a-0" -> "a".asVar,
    "a*a-0*(a+b+c/d)" -> ("a".asVar * "a".asVar),
    "a*a*a*1" -> ("a".asVar * "a".asVar * "a".asVar)
  )
