import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

var list = List(1,2,3)

val listImmutable = List(1,2,3)

list = list.filter(_ != 1)

val map1: Map[String, Double] = Map(("12345", 300), ("54321", 200), ("12345", 200))

case class Operation(opType: String, accNum: String, description: String, amount: Double, date: String)


val s:String = "22/08/2017"
val simpleDateFormat: SimpleDateFormat = new SimpleDateFormat("dd/mm/yyyy")
val date:Date = simpleDateFormat.parse(s)
val ans = new SimpleDateFormat("yyyy/mm/dd").format(date)

val date2 = new SimpleDateFormat("dd/mm/yyyy").format(date)


val date3 = LocalDate.parse("01/12/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
val newFormat = date3.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

val date5 = LocalDate.parse("01/12/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
val date6 = LocalDate.parse("01/11/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"))

date6.compareTo(date5)


object Operation {

  var list: List[Operation] = {
    List(
      Operation(
        "Debit",
        "12345",
        "compra na amazon",
        250.00,
        "07/08/17"
      ),
      Operation(
        "Debit",
        "34567",
        "deposito",
        500.54,
        "04/08/17"
      ),
      Operation(
        "Credit",
        "12345",
        "deposito",
        500.00,
        "04/08/17"
      ),

    )
  }
}


def balance(reqAccountNumber: String) = {

  val operations = Operation.list

  operations.filter(_.accNum == reqAccountNumber).foldLeft(0.0){ (balance, operation) =>
    val amount = operation.opType match {
      case "Credit" => {
        println("credit : " + operation.amount + " balance : " + balance)
        operation.amount
      }
      case "Debit" => {
        println("debit : " + operation.amount + " balance : " + balance)
        -operation.amount
      }
    }
    balance + amount
  }
}

balance("12345")


val numbers = List(5, 4, 8, 6, 2)
numbers.fold(0) { (z, i) =>
  z + i
}


class Foo(val name: String, val age: Int, val sex: Symbol)

object Foo {
  def apply(name: String, age:Int, sex: Symbol) = new Foo(name, age, sex)
}

val fooList = Foo("Yun Lee", 30, 'male) ::
              Foo("Yeonhoo Lee", 30, 'male) ::
  Foo("Incontinentia Buttocks", 37, 'female) ::
  Nil

val stringList = fooList.foldLeft(List[String]()) { (z, f) =>
  val title = f.sex match {
    case 'male => "Mr."
    case 'female => "Ms."
  }
  z :+ s"$title ${f.name}, ${f.age}"
}
