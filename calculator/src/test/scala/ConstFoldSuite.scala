import calculator.*
import FullExpr.*

trait ConstFoldSuite extends munit.FunSuite:
  import Printers.*

  for (i, o) <- ConstFoldSuite.tests do
    test(s"constfold: const-folding $i should be ${o.show} (1pt)") {
      assertEquals(FullDriver.constfold(i).get, o)
    }

object ConstFoldSuite:
  val tests: List[(String, FullExpr)] = List(
    "1+1" -> 2.asNum,
    "1-1" -> 0.asNum,
    "2/2" -> 1.asNum,
    "10*10" -> 100.asNum,
    "1+2+3" -> 6.asNum,
    "1+2*3" -> 7.asNum,
    "a+2*3" -> ("a".asVar + 6.asNum),
    "a*(2*3)" -> ("a".asVar * 6.asNum),
    "a*(2*3*4)" -> ("a".asVar * 24.asNum),
    "a+b+c*(1-2)" -> ("a".asVar + "b".asVar + "c".asVar * -1.asNum)
  )
