package calculator

import Pos.*

enum TokenType:
  case Plus
  case Minus
  case Star
  case Slash
  case LeftParen
  case RightParen
  case Literal
  case Var
  case Equal

case class Token(tpe: TokenType, content: String) extends WithPos
