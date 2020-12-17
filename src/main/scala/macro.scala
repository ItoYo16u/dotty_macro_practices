package dummy
import scala.deriving._
import scala.quoted._
import scala.quoted.Type

inline def debugSingle(inline expr: Any):Unit = ${debugSingleImpl('expr)}
def debugSingleImpl(expr: Expr[Any])(using Quotes): Expr[Unit] =
  '{println("value of " + ${Expr(expr.show)} + " is " +  $expr)}

inline def showTypeAsStr(inline expr:Any):Unit = ${showTypeAsStr('expr)} 
def showTypeAsStr(expr: Expr[Any])(using Quotes):Expr[Unit] = {
  '{ println( ${Expr(Type.show[Box]) }) }
}
