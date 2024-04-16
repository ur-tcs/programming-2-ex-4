import util.{Try, Failure, Success}

import calculator.*
import Printers.*
import Simplifier.*

class REPL(initMode: REPL.Mode):
  import REPL.*
  import Mode.*

  lazy val interactors: Map[Mode, Interactor] = Map(
    EvalTiny -> new EvalTinyInteractor,
    EvalBasic -> new EvalBasicInteractor,
    EvalFull -> new EvalFullInteractor,
    ConstFold -> new ConstFoldInteractor,
    Arith -> new ArithInteractor,
    Simp -> new SimpInteractor,
    SmallStep -> new SmallStepInteractor
  )

  private var myMode: Mode = initMode

  def setMode(m: Mode): this.type =
    myMode = m
    this

  private def getInteractor: Interactor = interactors(myMode)

  def interact(input: String): Unit =
    if !input.isEmpty then
      val res = Try:
        getInteractor.interact(input)
      res match
        case Failure(e) => println(s"Error:\n${e.toString}")
        case Success(_) => ()

  private def changeMode(m: Mode) =
    setMode(m)
    println(s"Mode has been changed to $m")

  def loop(): Unit =
    val source = scala.io.StdIn.readLine(text = "> ")
    var isEnded = false

    def action(cmd: String): Unit = cmd match
      case REPL.ModeString(mode) => changeMode(mode)
      case "quit" | "exit"       => isEnded = true
      case cmd                   => println(s"Unknown command: $cmd")

    source.trim match
      case s if s.startsWith(":") => action(s.substring(1))
      case source                 => interact(source)
    if !isEnded then loop()

object REPL:
  enum Mode:
    case EvalTiny
    case EvalBasic
    case EvalFull
    case ConstFold
    case Arith
    case Simp
    case SmallStep

  object Mode:
    def fromString(mode: String): Option[Mode] = mode match
      case "tiny"      => Some(EvalTiny)
      case "basic"     => Some(EvalBasic)
      case "full"      => Some(EvalFull)
      case "constfold" => Some(ConstFold)
      case "algebraic" => Some(Arith)
      case "simp"      => Some(Simp)
      case "smallstep" => Some(SmallStep)
      case _           => None

  object ModeString:
    def unapply(s: String): Option[Mode] = Mode.fromString(s)

  trait Interactor:
    def interact(input: String): Unit

  class NotSupportedInteractor(name: String) extends Interactor:
    def interact(input: String): Unit = throw NotImplementedError(name)

  class EvalTinyInteractor extends Interactor:
    def interact(input: String): Unit =
      val result = TinyDriver.evaluate(input).get
      println(result)

  class EvalBasicInteractor extends Interactor:
    def interact(input: String): Unit =
      val result = BasicDriver.evaluate(input).get
      println(result)

  class EvalFullInteractor extends Interactor:
    import FullEvaluator.*
    var myCtx: Ctx = empty

    def interact(input: String): Unit =
      val result = FullDriver.parseDef(input).get
      result match
        case (name, e) =>
          val result = FullDriver.evaluate(e, myCtx).get
          result match
            case FullEvalResult.Ok(v) =>
              println(s"$name = $v")
              myCtx = cons(name, v, myCtx)
            case err => err.get
        case e => println(FullDriver.evaluate(input, myCtx).get)

  class ConstFoldInteractor extends Interactor:
    def interact(input: String): Unit =
      val result = FullDriver.constfold(input).get
      println(result.show)

  class ArithInteractor extends Interactor:
    def interact(input: String): Unit =
      val result = FullDriver.algebraic(input).get
      println(result.show)

  class SimpInteractor extends Interactor:
    def interact(input: String): Unit =
      val result = FullDriver.simplify(input).get
      println(result.show)

  class SmallStepInteractor extends Interactor:
    def interact(input: String): Unit =
      val steps = TinyDriver.reduce(input).get
      steps.zipWithIndex.foreach: (e, i) =>
        println(s"step #$i: ${e.embed.show}")

@main def main(mode: String*): Unit =
  var theMode: String = "tiny"
  mode.toList match
    case Nil      =>
    case m :: Nil => theMode = m
    case _        => println("too many arguments")
  REPL.Mode.fromString(theMode) match
    case None => println(s"unknown mode: $mode")
    case Some(mode) =>
      val repl = new REPL(mode)
      repl.loop()
