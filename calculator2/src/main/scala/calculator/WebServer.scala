package calculator

import cats.*
import cats.effect.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import org.http4s.*
import org.http4s.dsl.*
import org.http4s.headers.*
import org.http4s.implicits.*
import org.http4s.circe.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import com.comcast.ip4s.*
import scala.annotation.tailrec
import scala.util.{Try, Success, Failure}

import Printers.*
import GraphvizPrinter.*

object WebServer extends IOApp:
  val HTML_PATH = "src/main/html/dist/index.html"
  val JS_PATH = "src/main/html/dist/main.js"
  val CSS_PATH = "src/main/html/dist/css/main.css"

  enum CalcOutput:
    case Error(msg: String)
    case Ok(contents: List[Content])

  enum Content:
    case GraphvizFigure(title: String, source: String)
    case PlainText(title: String, text: String)
    case ReductionSeq(title: String, steps: List[(String, String)])

  enum ExprType:
    case Tiny()
    case Basic()
    case Full()

  object ExprTypeVar:
    import ExprType.*
    def unapply(s: String): Option[ExprType] = s match
      case "tiny"  => Some(Tiny())
      case "basic" => Some(Basic())
      case "full"  => Some(Full())
      case _       => None

  object ExprQueryParamDecoderMatcher extends QueryParamDecoderMatcher[String]("expr")
  object ExprTypeQueryParamDecoderMatcher extends QueryParamDecoderMatcher[String]("exprType")

  trait ExprHandler[K <: ExprType]:
    def process(e: String): CalcOutput = ???

  extension (mmx: Try[CalcOutput])
    def unwrap: CalcOutput = mmx match
      case Failure(exception) => CalcOutput.Error(exception.toString)
      case Success(value)     => value

  given ExprHandler[ExprType.Tiny] with
    import CalcOutput.*
    import Content.*

    override def process(e: String): CalcOutput =
      def go: Try[CalcOutput] = Try:
        val figure = TinyDriver.render(e).get
        val value = TinyDriver.evaluate(e).get
        val steps = TinyDriver.reduce(e).get
        val reprs = steps.map(e => (e.embed.show, e.toGraphviz))
        Ok(
          List(
            GraphvizFigure("AST", figure),
            PlainText("Evaluated", value.toString),
            ReductionSeq("Smallstep Evaluation", reprs)
          )
        )
      go.unwrap

  given ExprHandler[ExprType.Basic] with
    import CalcOutput.*
    import Content.*

    override def process(e: String): CalcOutput =
      def go: Try[CalcOutput] = Try:
        val expr = BasicDriver.parse(e).get
        val value = BasicDriver.evaluate(expr)
        val figure = expr.toGraphviz
        Ok(
          List(
            GraphvizFigure("AST", figure),
            PlainText("Evaluated", value.toString)
          )
        )
      go.unwrap

  given ExprHandler[ExprType.Full] with
    import CalcOutput.*
    import Content.*

    override def process(e: String): CalcOutput =
      def go: Try[CalcOutput] = Try:
        val block = FullDriver.parseBlock(e).get
        val result = FullDriver.evaluateBlock(block).get
        val resultStr = result.defs.map((n, v) => s"$n = $v").appended(result.expr.toString).mkString("\n")
        val expr = block.expr
        val constfolded = Simplifier.constfold(expr)
        val arith = Simplifier.algebraic(expr)
        val simplified = Simplifier.simplify(expr)
        Ok(
          List(
            GraphvizFigure("AST", expr.toGraphviz),
            PlainText("Evaluated", resultStr),
            PlainText("Constfolded", constfolded.show),
            GraphvizFigure("Constfolded (AST)", constfolded.toGraphviz),
            PlainText("Algebraic Simplified", arith.show),
            GraphvizFigure("Algebraic Simplified (AST)", arith.toGraphviz),
            PlainText("Fully Simplified", simplified.show),
            GraphvizFigure("Fully Simplified (AST)", simplified.toGraphviz)
          )
        )
      go.unwrap

  def webuiRoutes[F[_]: Monad]: HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl.*

    def staticFile(path: String, mediaType: MediaType): F[Response[F]] =
      val source = scala.io.Source.fromFile(path)
      val html =
        try source.mkString
        finally source.close()
      Ok(html, `Content-Type`.apply(mediaType))

    HttpRoutes.of[F] {
      case GET -> Root                      => staticFile(HTML_PATH, MediaType("text", "html"))
      case GET -> Root / "main.js"          => staticFile(JS_PATH, MediaType("text", "javascript"))
      case GET -> Root / "css" / "main.css" => staticFile(CSS_PATH, MediaType("text", "css"))
      case GET -> Root / "calc" / ExprTypeVar(exprType) / expr =>
        println(s"Got request: $expr, type = $exprType")
        val handler = exprType match
          case ExprType.Tiny()  => summon[ExprHandler[ExprType.Tiny]]
          case ExprType.Basic() => summon[ExprHandler[ExprType.Basic]]
          case ExprType.Full()  => summon[ExprHandler[ExprType.Full]]
        val res = handler.process(expr)
        Ok(res.asJson)
    }

  def webuiHttpApp[F[_]: Monad]: HttpApp[F] = webuiRoutes.orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    val app = webuiHttpApp[IO]
    val server = EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(app)
      .build

    server.use(_ => IO.never).as(ExitCode.Success)
