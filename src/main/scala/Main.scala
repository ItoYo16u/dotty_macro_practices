
package dummy

case class Box(id:String)
case class Person(id: String,name: String,age: Int,box:Box)

object Main {

  import dummy.Macro._
  gen[Box]
  
  def main(args: Array[String]): Unit = {
    val b = Box("x")
    println(Person("a","b",1,b).toJson)
  
  }


}
