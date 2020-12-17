package dummy

import scala.annotation.targetName
import scala.reflect._
import scala.compiletime.{erasedValue, summonInline, summonAll => CTSummonAll}
import scala.deriving._
import scala.deriving.ArrayProduct
import scala.quoted._



trait ToJson[A] {

    def   toJson(a:A) : String
}

object ToJson {

  inline given ToJson[Int] {
      override def toJson(a:Int): String = s"json of $a"
  }

  inline given ToJson[String] {
      override def toJson(a:String): String = s""""json of $a""""
  }

  def extractLabelsAsString[T : Type](using q: Quotes):List[Expr[String]] = Type.of[T] match {
    case  '[t *: ts] => Expr(Type.show[t]) :: extractLabelsAsString[ts]  // type.show[t]
    case '[Tuple$package.EmptyTuple] => Nil
  }
  // erasedValue[T] returns T. Useful when retrive T inline.
  // when T is Person's mirrored elem types, (String,String,Int) is given.
  def summonAll[T : Type](using q: Quotes): List[Expr[ToJson[_]]] = Type.of[T] match {
    // summonInline used to search implicits inline.
    // WARN: Any*:Any
    // fieldの型に対応するToJsonをsummon -> TypeのListを返す
    // ie. (String,String,Int) -> List(ToJson[String],ToJson[String],ToJson[Int])
    case '[String *: ts] => '{summon[ToJson[String]]} :: summonAll[ts]
    case '[Int *: ts] => '{summon[ToJson[Int]]} :: summonAll[ts]
    case '[t *: ts] => derived[t] :: summonAll[ts]
    case '[Tuple$package.EmptyTuple]  => Nil
  }
  // derived is an entry point for companion objects generated from drives clause.
  // derived[A] means derived by [A] (?)

  inline def toJsonForCaseClasses[T](body: (T)=> String):ToJson[T] = {
    new ToJson[T]{
        override def toJson(a:T):String = body(a)
      }
  }
  // ドキュメントだとusing(Quotes)になっているが、 (using q:Quotes)にしないとエラーになる
  inline def derived[A: Type](using q:Quotes): Expr[ToJson[A]] = {
    import quotes.reflect._
    // `summon` is for TypeClass instance.(?)
    val expression: Expr[Mirror.Of[A]] = Expr.summon[Mirror.Of[A]].get
    expression match {
        // 以下のようにExprに対してパターンマッチできる
        // A がcase class,case object等の場合
      case '{ $m: Mirror.ProductOf[A] {type MirroredElemLabels = elementLabels;type MirroredElemTypes = elementTypes}}=>

        val elementResolver = summonAll[elementTypes]

        val callback: (Expr[A])=> Expr[String] = (a)=> {
        val values = elementResolver.zipWithIndex.foldLeft(Expr(Nil) :Expr[List[String]]){ case (acc,(_,index))=>
          val label = '{$a.asInstanceOf[Product].productElementName(${Expr(index)})}
          val value = '{$a.asInstanceOf[Product].productElement(${Expr(index)})}
          // 以下でvalue.toJson か ToJson[].toJson(value)  を呼びたい
          val pair = '{""""""" + ${label}+ """"""" + ":" + ${value} }
          '{  ${pair} :: $acc }
        }
        '{"{" +  $values.mkString(",") + "}"}
        }
        '{
          toJsonForCaseClasses((a:A)=>${callback('a)})
        }
      case _ => ???
    }
  }
}

object Macro {
  extension [T](inline x:T){
    inline def toJson(using jsonizer:ToJson[T]):String = jsonizer.toJson(x)
  }

  implicit inline def gen[T]:ToJson[T]= ${genImpl[T]}
  def genImpl[T](using Quotes)(using Type[T]):Expr[ToJson[T]] = ToJson.derived[T]
}



/*
* references
* - https://gist.github.com/fsarradin/178876f079f29aa1092a9326899043ef Mar 29,2019.
* - https://stackoverflow.com/questions/62853337/how-to-access-parameter-list-of-case-class-in-a-dotty-macro Answered Jul 12,2020
* - http://dotty.epfl.ch/docs/reference/contextual/derivation.html
*
* */