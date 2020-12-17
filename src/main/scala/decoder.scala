import scala.reflect._
import scala.deriving._
import scala.quoted._
import scala.quoted.{Type => QuotedType}
import scala.compiletime._

case class ParseError(str: String, msg: String)

trait Decoder[T]{
  def decode(str:String): Either[ParseError, T]
}

object Decoder {
  inline given  Decoder[String] = new Decoder[String] {
    override def decode(str: String): Either[ParseError, String] = Right(str)
  }

  inline given Decoder[Int] = new Decoder[Int] {
    override def decode(str: String): Either[ParseError, Int] =
      str.toIntOption.toRight(ParseError(str, "value is not valid Int"))
  }
  
  
  inline def derived[T](using m: Mirror.Of[T]): Decoder[T] = {
    val elemInstances = summonAll[m.MirroredElemTypes]
    val labels = summonFields[m.MirroredElemLabels]
    inline m match {
      case p: Mirror.ProductOf[T] => productDecoder(p, elemInstances)
      case s: Mirror.SumOf[T]     => ???
    }
  }

  inline def summonAll[T <: Tuple]: List[Decoder[_]] = inline erasedValue[T] match {
    case _: Tuple$package.EmptyTuple /* EmptyTuple in 0.25 */ => Nil
    case _: (t *: ts) => summonInline[Decoder[t]] :: summonAll[ts]
  }
  // ここでMirroredElemLabels Tupple("id","name","age") -> List["id","name","age"]にしたい
  inline def summonFields[T<: Tuple]: List[String] = inline erasedValue[T] match {
    case  _: Tuple$package.EmptyTuple => Nil
    case _: (t*:ts) =>  "" :: summonFields[ts]
  }

  def productDecoder[T](p: Mirror.ProductOf[T], elems: List[Decoder[_]]): Decoder[T] =
    new Decoder[T] {
      def decode(str: String): Either[ParseError, T] = {
        elems.zip(str.split(','))
          .map(_.decode(_).map(_.asInstanceOf[AnyRef]))
          .sequence
          // p.fromProduct generates T class from tupple of decoded values. T is case class A here.
          .map(ts => p.fromProduct(Tuple.fromArray(ts.toArray)))
      }
    }
  extension[E,A](es:List[Either[E,A]]) def sequence: Either[E,List[A]]= traverse(es)(x => x)

  def traverse[E,A,B](es: List[A])(f: A => Either[E, B]): Either[E, List[B]] =
    es.foldRight[Either[E, List[B]]](Right(Nil))((h, tRes) => map2(f(h), tRes)(_ :: _))

  def map2[E, A, B, C](a: Either[E, A], b: Either[E, B])(f: (A, B) => C): Either[E, C] =
    for { a1 <- a; b1 <- b } yield f(a1,b1)
}

case class A(i: Int, s: String) derives Decoder

@main def test = {
  println(summon[Decoder[A]].decode("10,abc"))//Right(A(10,abc))
  println(summon[Decoder[A]].decode("xxx,abc"))//Left(ParseError(xxx,value is not valid Int))
  // println(summon[Decoder[A]].decode(","))
}