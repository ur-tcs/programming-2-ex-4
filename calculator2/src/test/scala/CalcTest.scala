import calculator.*
import FullExpr.*

val DELTA = 0.00001

class CalcTest extends ToStringSuite, ToPolishSuite, TinySuite, BasicSuite, EvalVarSuite,
      ConstFoldSuite, ArithSuite, SimpSuite, SmallStepSuite,
      HoursTest
