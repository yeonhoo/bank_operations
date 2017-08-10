import java.time.LocalDate
import java.time.format.DateTimeFormatter

import controllers.Operation
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Operation(opType: String, accNum: String, description: String, amount: Double, date: LocalDate)

case class SimpleInfo(description: String, amount: Double)
case class SimpleInfoByDate(date: LocalDate, info: List[SimpleInfo])

object Operation {

  var list: List[Operation] = {
    List(
      Operation(
        "Debit",
        "12345",
        "compra na amazon",
        250.00,
        LocalDate.parse("05082017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "34567",
        "deposito",
        500.54,
        LocalDate.parse("03082017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "12345",
        "deposito",
        500.00,
        LocalDate.parse("01082017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "12345",
        "deposito",
        500.00,
        LocalDate.parse("02082017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "12345",
        "deposito",
        500.00,
        LocalDate.parse("25072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "12345",
        "transferencia",
        2000.00,
        LocalDate.parse("25072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      )
    )
  }
}


implicit val simpleInfo: Writes[SimpleInfo] = (
  (JsPath \ "description").write[String] and
    (JsPath \ "amount").write[Double]
  )(unlift(SimpleInfo.unapply))


implicit val simpleInfobyDateWrites: Writes[SimpleInfoByDate] = (
  (JsPath \ "date").write[LocalDate] and
    (JsPath \ "info").write[List[SimpleInfo]]
  )(unlift(SimpleInfoByDate.unapply))


def balanceByDate(account: String, date: String) = {
  val date = LocalDate.parse(date, DateTimeFormatter.ofPattern("ddMMyyyy"))
  val operations = Operation.list

  operations.filter{ op =>
    op.accNum == account && op.date.compareTo(date) <= 0
  }.foldLeft(0.0) { (balance, operation) =>
    val amount = operation.opType match {
      case "Credit" => operation.amount
      case "Debit" => -operation.amount
    }
    balance + amount
  }
}




def statement(account: String, dateFrom: LocalDate, dateTo: LocalDate) = {
  val operations = Operation.list

  val data = operations.filter { op =>

    //println("operation date : " + op.date + " dateFrom : " + dateFrom + " compare : " + op.date.compareTo(dateFrom))
    //println("operation date : " + op.date + " dateTo : " + dateTo + " compare : " + op.date.compareTo(dateTo))
    op.accNum == account &&
      op.date.compareTo(dateFrom) >= 0 &&
      op.date.compareTo(dateTo) <= 0
  }.groupBy(_.date).map {
    case (date, xs) => {
      SimpleInfoByDate(date, xs.map(e => SimpleInfo(e.description, e.amount)))
    }
  }.toList.sortWith{ (op1, op2) => op1.date.compareTo(op2.date) <= 0 }


  val transformedData = Json.toJson(data)
  transformedData
}



val process = statement("12345",
  LocalDate.parse("01/05/2017", DateTimeFormatter.ofPattern("dd/MM/yyyy")),
  LocalDate.parse("10/08/2017", DateTimeFormatter.ofPattern("dd/MM/yyyy"))
)



println(balanceByDate("12345", "02082017"))
