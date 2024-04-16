import calculator.*
import FullExpr.*

trait ArithSuite extends munit.FunSuite:
  import Printers.*

  for (i, o) <- ArithSuite.tests do
    test(s"algebraic: simplify $i should be ${o.show} (0.5pt)") {
      assertEquals(FullDriver.algebraic(i).get, o)
    }

object ArithSuite:
  val tests: List[(String, FullExpr)] = List(
    "0+a" -> "a".asVar,
    "0+a/b" -> ("a".asVar / "b".asVar),
    "a+0" -> "a".asVar,
    "(a+b)+0" -> ("a".asVar + "b".asVar),
    "0-a" -> "a".asVar.neg,
    "a-0" -> "a".asVar,
    "(a+b)-0" -> ("a".asVar + "b".asVar),
    "(a+b)-(0*a)" -> ("a".asVar + "b".asVar),
    "0*a" -> Number(0),
    "0*(a+2)" -> Number(0),
    "a*0" -> Number(0),
    "(a+2)*0" -> Number(0),
    "1*a" -> "a".asVar,
    "1*(a+b)" -> ("a".asVar + "b".asVar),
    "(0*a+1-0)*(a+b)" -> ("a".asVar + "b".asVar),
    "a*1" -> "a".asVar,
    "(a+b)*1" -> ("a".asVar + "b".asVar),
    "(a+b)*(1*1+0*a)" -> ("a".asVar + "b".asVar),
    "a/1" -> "a".asVar,
    "(a+b)/1" -> ("a".asVar + "b".asVar),
    "a-a" -> 0.asNum,
    "(a+a)-(a+a)" -> 0.asNum,
    "(a-a+1)*a*a" -> ("a".asVar * "a".asVar),
    "(a-a+1)*a*a-a*a" -> 0.asNum,
    "1*a-0" -> "a".asVar,
    "a*a-0*(a+b+c/d)" -> ("a".asVar * "a".asVar),
    "a*a*a*1" -> ("a".asVar * "a".asVar * "a".asVar),
    "a*(1+2)" -> ("a".asVar * (1.asNum + 2.asNum)),
    "a*(1+2)+(b-b)" -> ("a".asVar * (1.asNum + 2.asNum)),
    "a*(1+2)*(b*(c-c)+1)+(b-b)" -> ("a".asVar * (1.asNum + 2.asNum))
  )
