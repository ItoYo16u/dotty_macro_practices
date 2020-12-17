
package dummy

case class Box(id:String)
case class Person(id: String,name: String,age: Int,box:Box)

object Main {

  import dummy.Macro._
  
  def main(args: Array[String]): Unit = {
    val b = Box("y")

    println(Person("a","c",1,b).toJson)
  
  }


}
