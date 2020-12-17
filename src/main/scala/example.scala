
// summon[Example[Int]].toJson: Int=> String
// = Lamdba$***hash*** 
trait Example[T]{
  extension (t:T) def toStringwithExclamation : String
}

given Example[Int] { // given_Example_int.type = given_Example_int$***hash***
    extension (t:Int) def toStringwithExclamation : String = s"this is int $t !!!"
}
given Example[String] { // given_Example_string.type = given_Example.string$***hash***
  extension (t:String) def toStringwithExclamation : String = s"this is string: $t !!!"
}
// 1.toStringWithExclamation => "1 !!!"
// "1".toStringWithExclamation => "1 !!!"
// (2.0).toStringWithExclamation => Fails