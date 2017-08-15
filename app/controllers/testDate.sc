import java.time.LocalDate
import java.time.format.DateTimeFormatter

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Operation(opType: String, accNum: String, description: String, amount: Double, date: LocalDate)

case class SimpleInfo(description: String, amount: Double)
case class SimpleInfoByDate(date: LocalDate, info: List[SimpleInfo])

case class DebtPeriod(start: LocalDate, end: Option[LocalDate], principal: Double)

object Operation {

  var list: List[Operation] = {
    List(

      Operation(
        "Credit",
        "12345",
        "Deposit",
        1000.00,
        LocalDate.parse("15102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Purchase on Amazon",
        3.34,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Purchase on Uber",
        45.23,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "Withdrawal",
        180.00,
        LocalDate.parse("17102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),

      Operation(
        "Debit",
        "12345",
        "Purchase flight ticket",
        800.00,
        LocalDate.parse("18102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),

      Operation(
        "Credit",
        "12345",
        "Deposit",
        100.00,
        LocalDate.parse("25102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      )
      ,

      Operation(
        "Debit",
        "12345",
        "Deposit",
        300.00,
        LocalDate.parse("30102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),

      Operation(
        "Debit",
        "12345",
        "Deposit",
        400.00,
        LocalDate.parse("03112017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),

      Operation(
        "Credit",
        "12345",
        "Deposit",
        100.00,
        LocalDate.parse("05112017", DateTimeFormatter.ofPattern("ddMMyyyy"))
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

implicit val debtPeriodWrites: Writes[DebtPeriod] = (
  (JsPath \ "start").write[LocalDate] and
    (JsPath \ "end").write[Option[LocalDate]] and
    (JsPath \ "principal").write[Double]

  )(unlift(DebtPeriod.unapply))


def balanceByDate(account: String, d: String) = {
  val date = LocalDate.parse(d, DateTimeFormatter.ofPattern("ddMMyyyy"))
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

val list = List(
  (LocalDate.parse("10102017", DateTimeFormatter.ofPattern("ddMMyyyy")), 2000),
  (LocalDate.parse("15102017", DateTimeFormatter.ofPattern("ddMMyyyy")), 3000),
  (LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy")),-200),
  (LocalDate.parse("17102017", DateTimeFormatter.ofPattern("ddMMyyyy")), 300),
  (LocalDate.parse("18102017", DateTimeFormatter.ofPattern("ddMMyyyy")),-600),
  (LocalDate.parse("25102017", DateTimeFormatter.ofPattern("ddMMyyyy")),400)
)

def debtTest(account: String) = {
  val operations = Operation.list

  val result = operations.filter(_.accNum == account)
    .groupBy( op => op.date)
    .map {
      case (date, _) => date
    }.toList.sortWith( (date1, date2) => date1.compareTo(date2) <= 0)
    .map(date => Tuple2(date, balanceByDate(account, date.format(DateTimeFormatter.ofPattern("ddMMyyyy")))))
    //.toList.sortWith( (op1, op2) => op1._1.compareTo(op2._1) <= 0)
}


def debt(account: String) = {
  val operations = Operation.list

  val result = operations.filter(_.accNum == account)
    .groupBy( op => op.date)
      .map {
        case (date, _) => date
      }.toList.sortWith( (date1, date2) => date1.compareTo(date2) <= 0)
    .map(date => Tuple2(date, balanceByDate(account, date.format(DateTimeFormatter.ofPattern("ddMMyyyy")))))
    //.toList.sortWith( (op1, op2) => op1._1.compareTo(op2._1) <= 0)
    .sliding(2).filter{ p=>
      println("primeiro : " + p.lift(0).get._2 + "  Date : " + p.lift(0).get._1)
      println("segundo : " + p.lift(1).get._2 + "  Date : " + p.lift(1).get._1)

    (p.lift(0).get._2 < 0 && p.lift(1).get._2 > 0) ||
      (p.lift(0).get._2 < 0 && p.lift(1).get._2 < 0)
  }
    .flatten.grouped(2).toList

  val lastBalance = result.last.last
  println("last balance  mesmo ? : " + lastBalance)

  val finalResult = result.map{m =>
      DebtPeriod(m(0)._1, Some(m(1)._1.minusDays(1)), m(0)._2) } :+ DebtPeriod(lastBalance._1, None, lastBalance._2)

  Json.toJson(finalResult)
}

debt("12345")

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
