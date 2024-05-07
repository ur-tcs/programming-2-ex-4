# Programming 2 - Exercise 4 : Understand and Evaluate Arithmetic Expressions

In this exercise, you will implement functions that relate to the evaluation and simplification of arithmetic expressions. You will also implement a parser that takes a string, for example `1 + 2 - 5`, as input and turns it into an abstract syntax tree that can then be evaluated.

The most important parts of the exercise are marked with ⭐️. 

## Abstract Syntax Trees

Before we start programming, we should first take a moment to think about how arithmetic expressions are structured.  Looking back at *Introduction to Theoretical Computer Science I*, we have learned that arithmetic expressions form a context-free language that can be constructed using a grammar. Parse trees, also called concrete syntax trees, are a tool that helps us understand the structure of an expression. (You do not need to have passed *Introduction to Theoretical Computer Science I* in order to do this exercise!)

As such, arithmetic expressions are a minimal form of a programming language. Surprisingly, the methodologies and principles employed to construct the calculator in this lab bear significant resemblance to those utilized in programming language interpreters and compilers.

How does a calculator evaluate an expression? Let’s start with a concrete example: `1 + 2 * 3`. The calculator’s first job is to *parse* it. Parsing is similar to understanding the structure of the expression: the calculator scans the text, understands how it forms an expression, and then turns it into a so-called *abstract syntax tree (AST)*.

**Research Exercise:** Look up the difference between a concrete syntax tree and an abstract syntax tree.

For instance, the AST of `1 + 2 * 3` looks like this: 

```
    +
  /   \
 1     *
     /   \
    2     3
```

* The parent nodes, such as `+` and `*`, represent operations.
* The leaf nodes represent numbers. As you advance, these could represent variables as well.
* Each parent node has two child nodes which stand for the operands. For instance, the `+` node branches out to the node for `1` and the subtree for `2 * 3`.

**Try it yourself:** Construct an AST for the expression `-(1 * 2 + 3 * (4 + 5))` (using pen and paper, or a different graphics tool of your choice. No need to implement anything for now!).


After getting a feel for ASTs on paper, we can now try to implement them. In the previous exercises, you have encountered Scala's `enums`. Enums can encode recursive structures, which is perfect for trees. The following enum represents ASTs. 

```Scala
enum Expr:
  case Number(v: Double)
  case Add(e1: Expr, e2: Expr)
  case Minus(e1: Expr, e2: Expr)
  case Mul(e1: Expr, e2: Expr)
  case Div(e1: Expr, e2: Expr)
  case Neg(e: Expr)
```

The enum mirrors the structure of ASTs with remarkable fidelity:
* An `Add` case corresponds to a `+` node, with its two fields, `e1` and `e2`, representing the two children respectively.
* A `Number` case is a leaf node.

As a concrete example, to represent the AST of `1 + 2 * 3`, you would write:

```Scala
Add(Number(1.0), Mul(Number(2.0), Number(3.0))).
```

**Question:** How are the ASTs of `1 + 2 * -(3 + 4)` and `-(1 * 2 + 3 * (4 + 5))` represented in Scala?

<details>
<summary> Solution </summary> 

For the expression `1 + 2 * -(3 + 4)`, the Scala representation is:

```Scala
Add(Number(1.0), Mul(Number(2.0), Neg(Add(Number(3.0), Number(4.0)))))
```

For the expression `-(1 * 2 + 3 * (4 + 5))`, the Scala representation is:

```Scala
Neg(Add(Mul(Number(1.0), Number(2.0)), Mul(Number(3.0), Add(Number(4.0), Number(5.0)))))
```

</details><br/>

## Exercise setup

For this exercise, you will not be supplied a skeleton that you can download. Instead, we encourage you to start a project from scratch. You can do this however you like, feel free to experiment as long as your project allows you to do the exercises below. For those feeling less adventurous, we have included a list of instructions.

<details>
<summary> Starting a simple project from scratch </summary>

VSCode and Metals will actually do most of the work for you. A quick glance at https://code.visualstudio.com/api/ux-guidelines/overview can be helpful for navigating the user interface as you follow the instructions below.

* Create a new folder for your project. If you manage your exercises as recommended in exercise 0, this means creating a folder named `programming-2-ex-4` in the parent folder `ProgrammingII`.
* Start VSCode and click on the Metals icon in the Activity Bar.
* Among the build commands that are now displayed in the Side Bar, select "New Scala Project".
* You will now be offered a bunch of different templates. Choose `scala/scala3.g8`, since `scala/hello-world.g8` will not be in Scala 3! 
* Next, select the folder you created in step 1.
* Now, you're asked to choose a name for your project. If you don't feel like making decisions today, name it "calculator".
* Done! VSCode will ask whether you want to open your project in a new window and whether you want to import the build. Then, you only need to wait a little while your new project is set up for you. 

</details><br/>

## Warmup: Printing Exercises

### Printer 1: `show`

**Your first task** is to implement a function `show` that takes an arithmetic expression as input, and prints the corresponding Scala expression as seen above. For example, `show(1+2)` should print the expression `Add(Number(1.0),Number(2.0))`. In fact, this is how the built-in `toString` method prints the expression enum.

Here is the skeleton to be completed:

```Scala
  def show(e: Expr): String =
    "42"
```

<details>
<summary> Solution </summary>

```Scala
  def show(e: Expr): String =
    e match
      case Number(value) => f"Number($value)"
      case Add(e1, e2)   => f"Add(${show(e1)},${show(e2)})"
      case Minus(e1, e2) => f"Minus(${show(e1)},${show(e2)})"
      case Mul(e1, e2)   => f"Mul(${show(e1)},${show(e2)})"
      case Div(e1, e2)   => f"Div(${show(e1)},${show(e2)})"
      case Neg(e)        => f"Neg(${show(e)})"
```

</details><br/>

### Printer 2: Polish Notation

This second printer can safely be skipped if you feel like you do not have enough time for all tasks. The following tasks do not rely on it.

In Exercise 2, you have encountered polish notation. Your second task is to implement a function `toPolish` that prints expressions in polish notation. 

For example, `1 + 1` should be printed as `+ 1 1` and `1 + 2 * 3` be printed as `+ 1 * 2 3`.

Specially, use `--` for negation to disambiguate negation and subtraction. For instance, `(1 + -2) - 3` should be printed as `- + 1 -- 2 3`.

**Question:** If `-` stands for both negation and subtraction, can a polish string be ambiguous?

<details>
<summary> Solution </summary>

`- + 1 - 2 3` is an ambiguous polish string. Depending on how you interpret `-`, it can be parsed to either `- (1 + (2 - 3))` or `(1 + -2) - 3`.

</details><br/>

**Now, complete the following function:** 

```Scala
  /** Print the expression in Polish notation. */
  def toPolish(e: Expr): String =
    "42"
```

## Constructing a Parser

You will implement the parser for this project yourself, using the library `scala-parser-combinators`. Documentation of the library can be found at https://github.com/scala/scala-parser-combinators. This also tells you how to integrate the library into your project.

<details>
<summary> Show me how! </summary>

The package is added by adding the line
```Scala
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"
```

to your `build.sbt` file. Since you also need munit (which, if you set up your project following the directions above, is already included), straightforward copy-pasting may lead to an error message. In this case, you can try adding a list of dependencies instead:

```Scala
libraryDependencies ++= Seq(
  "org.scalameta" %% "munit" % "0.7.29" % Test,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0"
)
```

</details><br/>

Now we can construct a parser!

Your first step should be to write down a grammar for the language you want to parse, in this case the language of arithmetic expressions. We will walk through a smaller example together so you can familiarize yourself with the syntax. 

If we wanted to generate the language of all arithmetic expressions that only consist of numbers and the symbol `+`, our grammar only needs the following production rules: 

    <Expr> ::= <Num> "+" <Expr> | <Num>
    <Num> ::= "floatingPointNumber"

Here, `<Expr>` and `<Num>` denote the only nonterminal symbols (here, `floatingPointNumber` represents any element from the infinite set of floating point numbers and can be considered a terminal symbol). `<Expr>` is the starting symbol. All possible productions are listed after `::=`, seperated by `|`. The expression `1+2`, for example, can be derived by the sequence \<Expr\> &#8594; \<Num\> "+" \<Expr\> &#8594; "1 +" \<Expr\>  &#8594; "1 +" \<Num\>  &#8594; "1 + 2". In this minimal example it does not matter whether the rule is `<Expr> ::= <Expr> "+" <Num>` or `<Expr> ::=  <Num> "+" <Expr>`, but it will be important when you move on to the `-` and `/` operators.

We are practically done, as Scala's combinator parsers can closely mirror the underlying grammar. A first combined parser for our grammar could look like this:

```Scala
// import library
import scala.util.parsing.combinator._
// we build on the enum Expr from earlier
import Expr.* 

object MiniParser extends JavaTokenParsers {
  def expr: Parser[Any] = 
    num~"+"~expr |
    num
  def num: Parser[Any] = 
    floatingPointNumber 
} 
```

What happens here?

* Our parser inherits from the trait `JavaTokenParsers`, which provides the basic infrastructure for writing a parser. It also comes with some primitive parsers. One of those is `floatingPointNumbers`, which we have used and whose purpose is self-explanatory. 
* The definitions of the parsers `expr` and `num` represent the production rules of our grammar. Again, alternative productions are separated using `|`. Concatenation now has an explicit operator, `~`.
* `Parser[Any]` permits any return type, which is good for getting a first example running without having to look into the return types of the individual parsers just yet.
* `parseAll(expr, text)` applies the parser `expr` to the string `text`. Remember that `<Expr>` is the grammar's starting symbol. The parsers `num` and `floatingPointNumber` are not of interest to end users.

Let's test the parser!

```Scala
scala> MiniParser.parseAll(MiniParser.expr,"1+2+3+4+5")
[1.10] parsed: ((1~+)~((2~+)~((3~+)~((4~+)~5))))
```

`[1.10]` tells you that the input has been successfully parsed up to line 1, column 4. The result `((1~+)~((2~+)~((3~+)~((4~+)~5))))` is not very useful (yet): for humans, it is harder to read than the original input. For computers, it is still too disorganized to process. In a second step, we will see how to fix some of the problems we encountered.

**Question:** What happens if you change the line `num~"+"~expr |` to `expr~"+"~num |`? 

<details>
<summary> Solution </summary>

Attempting to parse something will cause your program to not terminate. That is because, as in most parsing libraries, symbols are read **left to right** and **the leftmost nonterminal symbol is always derived first**. This means that as of now, you can only parse grammars that are **right-recursive**.

</details><br/>

This is a very serious limitation, because arithmetic operations are **left-recursive**. We want the expression `1 + 2 + 3` to be read as `(1 + 2) + 3` and not `1 + (2 + 3)`. For the `+` operation, it does not make a difference, but it does when considering the `-` operator. `(1 - 2) - 3` is not the same as `1 - (2 - 3)`.

> [!TIP]
> If you need more time to understand this, try drawing the parse trees for the expression `1 + 2 + 3`, one time using the grammar above and one time using the grammar in which you have replaced the production rule `<Expr> ::=  <Num> "+" <Expr>` with `<Expr> ::= <Expr> "+" <Num>`. If you feed these different data structures to a computer program that wants to evaluate them, which part of the trees will it try to evaluate first?

We will now look at an "upgraded" version of the parser that fixes two of the problems we have encountered: Its output is ready to be processed by an `evaluate` function you will implement later, and it is able to handle left-recursive grammars.

```Scala
object MiniParser extends JavaTokenParsers with PackratParsers {
  lazy val expr: PackratParser[Expr] = 
    (expr<~"+")~num ^^ { case e1~e2 => Add(e1, e2) } |
    num
  lazy val num: PackratParser[Expr] = 
    floatingPointNumber ^^ { case x => Number(x.toDouble) }

  def parse(text: String) = parseAll(expr, text)
} 

def parse(text: String) = MiniParser.parse(text).get
```
Here's what's new:
* `PackratParsers` allows us to implement left-recursive grammars. We had to replace `def` with the more flexible `lazy val` when defining `expr` and `num`. 
* `PackratParser[Expr]` tells us the return type of the parser, which is `Expr`.
* `<~` (as well as `~>`) allows us to discard unnecessary parser results. It means "only remember the stuff I'm pointing at".
* We have used the operator `^^` to transform the result of a parser. For example, `floatingPointNumber` returns the string it parsed, not a number. If we want to apply `.toDouble` to this string, we can write `floatingPointNumber ˆˆ { case x => x.toDouble }`. We also need to wrap this number into a `Number()`expression.
* A string `s` can now be parsed with the command `parse(s)`, hiding redundant components.

If we test this new parser, we get:

```Scala
scala> parse("1+2+3+4+5")
Add(Add(Add(Add(Number(1.0),Number(2.0)),Number(3.0)),Number(4.0)),Number(5.0))
```

⭐️ **Task:** Construct a Parser `ExprParser` that can parse all arithmetic expressions. You can base your parser on `MiniParser`. Remember to define the grammar first! The grammar should reflect the order of arithmetic operations: 
* expressions in brackets `()` should be evaluated first,
* then `*` and `/`,
* then `+` and `-`. If two operations have the same precedence, the expressions should be evaluated left-to-right.

<details>
<summary> Hint for people who didn't do "Introduction to Theoretical Computer Science I" </summary>

Use this grammar:

    <Expr> ::= <Expr> "+" <Term> | <Expr> "-" <Term> | <Term>
    <Term> ::= <Term> "*" <Factor> | <Term> "/" <Factor> | <Factor>
    <Factor> ::= "floatingPointNumber" | "(" <Expr> ")"

</details><br/>

## Evaluation

### Basic Evaluator
If your parser is up and running, you can now try your hand at an evaluation function. The evaluation function should take an `Expr` as input and output its value. 

⭐️ **Task:** This is the current version of the `evaluate` function. As of now, it only outputs `42`. Make it work correctly. Also think about how you want to handle division by `0`.

```Scala
def evaluate(e: Expr): Double =
    42
```

<details>
<summary> Hint </summary>

Try implementing a smaller example first, for example the mini language we used earlier to explain parsers.

Testing is easier if you let your parser support you; `evaluate(parse("1+2-3"))` is way shorter than `Minus(Add(Number(1.0), Number(2.0)), Number(3.0))`.

</details><br/>

Congratulations! You can now parse and evaluate basic arithmetic expressions.

## Variables

Next, we want to include variables in our expressions. For this purpose, we need to adjust `Expr`s to also include variables:

```Scala
enum Expr:
  case Number(v: Double)
  case Add(e1: Expr, e2: Expr)
  case Minus(e1: Expr, e2: Expr)
  case Mul(e1: Expr, e2: Expr)
  case Div(e1: Expr, e2: Expr)
  case Neg(e: Expr)
  case Var(s: String)
```

⭐️ **Task:** Modify your parser from earlier so that it correctly parses arithmetic expressions with variables. The output should look like this: 

```Scala
> parse("1+x")
Add(Number(1.0),Var(x))
```

You may notice that, by this requirement, not every string is an appropriate name for a variable. For example, you should not name a variable `"1+2"`. As long as you only permit variable names that are unambiguous, your parser should work fine. You are free to come up with your own restrictions for variable names (for example, permit only variable names that consist of lowercase letters)... as long as you are capable of implementing them.

<details>
<summary> Hint </summary>

We have mentioned earlier that `JavaTokenParsers` comes with some primitive parsers: `ident`, `wholeNumber`, `decimalNumber`, `stringLiteral` and `floatingPointNumber`. Maybe one of them is of use? 

Their functions are documented in https://github.com/scala/scala-parser-combinators/blob/v1.0.7/shared/src/main/scala/scala/util/parsing/combinator/JavaTokenParsers.scala.

<details>
<summary> SUPER-HINT (this one uses two hint coins instead of one) </summary>

The parser `ident` parses strings that are valid Java identifiers, i. e., strings that start with a letter followed by an arbitrary number of letters and numbers. Use 

```Scala
ident ^^ { case x => Var(x.toString) }
```
.

</details><br/>

</details><br/>

## Simplification

Now that variables are included in our expressions, we are interested in simplification of expressions. For example, the expression `a/(2/2) + (5-4)*a` can be simplified to `0`. There are two types of simplification.

### Constant Folding

**Constant folding** is the simplification of constant sub-expressions. For example, `a + 2 * 3` can be constant folded into `a + 6`; `a + b * (3 + 3)` can be simplified to `a + b * 6`.

**Task:** Implement the function `constfold` below.

```Scala
/** Fold constant sub-expressions in values. */
def constfold(e: Expr): Expr =
  e
```

### Algebraic Simplification

**Algebraic simplification** is a bit more powerful, and a bit more complicated, than constant folding. It works by applying the following rules to expressions:

* 0 + e = e + 0 = e
*  0 - e = -e
*   e - 0 = e
*   0 * e = e * 0 = 0
*   1 * e = e * 1 = e
*   e / 1 = e
*   e - e = 0.

For instance, the expression `a * 0 - a / 1` can be simplified to `-a` with these rules.

**Quiz:** By applying these rules, what is the simplified form of `a * (b + 0) * c / 1 - a * b * c + a - 0`?

<details>
<summary> Solution</summary>

`a`

</details><br/>

**Task:** Implement the function `algebraic` below.

```Scala
// simplification rules
// 1. 0 + e = e + 0 = e
// 2. 0 - e = -e
// 3. e - 0 = e
// 4. 0 * e = e * 0 = 0
// 5. 1 * e = e * 1 = e
// 6. e / 1 = e
// 7. e - e = 0

/** Simplifiy expressions based on the listed algebraic rules. */
def algebraic(e: Expr): Expr =
  e
```

### Combined Simplification

You now know two distinct methods to simplify arithmetic expressions, and next you will create a new simplifier that combines both.

Sometimes, one method of simplification opens the door for another. For instance, the expression `(3 - 2) * (a * 1) - a` gets constant-folded to `1 * (a * 1) - a`, and algebraically simplified to `(3 - 2) * a - a`. But when the two simplifiers work together, they simplify the expression to `0.0`.

At first, the natural idea that comes to mind is to simply combine the above simplification methods, either as constfold-then-algebraic or as algebraic-then-constfold.

**Question:** Does this work?

<details>
<summary> Hint</summary>

Consider the expression `e = 0 * x - y * (1 - 1)`. What is the result of `algebraic(constfold(e))`? What about `constfold(algebraic(e))`?

</details><br/>

<details>
<summary> Solution</summary>


    algebraic(constfold(‘0 * x - y * (1 - 1)’))
    === algebraic(‘0 * x - y * 0’)
    === ‘0 - 0’

    constfold(algebraic(‘0 * x - y * (1 - 1)’))
    === constfold(‘0 - y * (1 - 1)’)
    === ‘0 - y * 0’

Unfortunately, neither composition fully simplifies the original expression.

The problem here is that in algebraic simplification may reveal new opportunities for constant-folding and vice versa.

</details><br/>

**Task:** Implement a new `simplify` method, covering all simplifications performed by `algebraic` and `constfold`.

<details>
<summary> Hint</summary>

To make sure that we simplify as much as possible, we must run both sets of rules together recursively. We then obtain an efficient function capable of simplifying the expression in a single traversal.

Think about which rules need to be applied first, and make sure it works on paper with simple examples before proceeding with the real implementation.

</details><br/>

```Scala
def simplify(e: Expr): Expr =
  e
```

## Small Step Evaluation

Earlier in this lab, you crafted an evaluator for expressions. Great job! These evaluators are often called big-step: They evaluate an input expression to its value in one function call. For instance, evaluating `1 + 2 + 3` returns `6` directly.

The opposite of big-step evaluation is small-step evaluation. It evaluates the expression step-by-step, giving you a sequence of intermediate state of the expression. For instance, a small-step evaluator first reduces `1 + 2 + 3` to `3 + 3`, then to `6`.

**Question:** How is `1 + 2 * 3` evaluated in small steps?

<details>
<summary>Solution</summary>

    1 + 2 * 3
    1 + 6
    7
</details><br/>

**Task:** Implement a small-step evaluator for `Expr`s. Your evaluator should perform one step and then return the "new" expression.

```Scala 
/** Evaluate the expression by one step. Return the expression as it is if it
  * has been fully evaluated.
  */
def step(e: Expr): Expr =
  e
```

The function should perform just one step of evaluation and should obey the following two principles:
* One step at a time: At each step, you should reduce one subtree whose operands are all numbers.
* Left-to-right: For operations with multiple operands that are not yet numbers, evaluate the left subtree first.

**Question:** Based on the above two principles, how should `1 * 2 + (1 + 2) * 4` be evaluated in small steps?

<details>
<summary>Solution</summary>

    1 * 2 + (1 + 2) * 4
    2 + (1 + 2) * 4
    2 + 3 * 4
    2 + 12
    14
</details><br/>


All done. Great job and see you next week!
