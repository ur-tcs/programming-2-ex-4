import calculator.*
import TinyExpr.*

/** Print function 1: re-implementing `toString` */
// An example expression
val e1 = Add(Number(0), Number(1))
// how the built-in `toString` prints the expression
val s1 = e1.toString
// how your implementation prints the expression
val s2 = TinyPrinters.show(e1)

/** Print function 2: Polish notation */
val s3 = TinyPrinters.toPolish(e1)

val e2 = TinyDriver.parse("(2 + -3) - 4").get
val s4 = TinyPrinters.toPolish(e2)
