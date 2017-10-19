import java.time.LocalDate
import java.time.format.DateTimeFormatter

import models.{DebtPeriod, Operation}
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Operation(opType: String, accNum: String, description: String, amount: Double, date: LocalDate)

case class SimpleInfo(description: String, amount: Double)
case class SimpleInfoByDate(date: LocalDate, info: List[SimpleInfo])

//case class DebtPeriod(start: LocalDate, end: Option[LocalDate], principal: Double)
case class DebtPeriod(principal: BigDecimal, start: LocalDate, end: Option[LocalDate])


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
  (JsPath \ "principal").write[BigDecimal] and
    (JsPath \ "start").write[LocalDate] and
    (JsPath \ "end").write[Option[LocalDate]]
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



def debt2(account: String) = {
  val operations = Operation.list
  //println(operations)

  val result = operations.filter(_.accNum == account)
    .groupBy( op => op.date)
    .map {
      case (date, _) =>
        (date, balanceByDate(account, date.format(DateTimeFormatter.ofPattern("ddMMyyyy"))))
    }.toList.sortWith( (date1, date2) => date1._1.compareTo(date2._1) <= 0)
      .sliding(2).filter{ p=>

    (p.lift(0).get._2 < 0 && p.lift(1).get._2 > 0) ||
      (p.lift(0).get._2 < 0 && p.lift(1).get._2 < 0)
  }.map( e => DebtPeriod(e(0)._2, e(0)._1, Some(e(1)._1.minusDays(1)))).toList


  val finalResult =
    if (result.last.principal < 0)
      result :+ DebtPeriod(result.last.principal, result.last.start, None)
    else
      result

  Json.toJson(finalResult)

}

debt2("12345")

def debt3(account: String) = {
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
  }.flatten

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
  println("result.last.  : " + result.last)

  println("last balance  mesmo ? : " + lastBalance)

  val finalResult = result.map{m =>
      DebtPeriod(m(0)._2, m(0)._1, Some(m(1)._1.minusDays(1)) ) } :+ DebtPeriod(lastBalance._2, lastBalance._1, None)

  Json.toJson(finalResult)
}

debt("12345")


def debtCalc(balanceByDate: List[(LocalDate, BigDecimal)]) = {

  val intermCalc = balanceByDate.sortWith( (date1, date2) => date1._1.compareTo(date2._1) <= 0)
    .sliding(2).filter{ p=>
    val firstBalance = p.lift(0).get._2
    val secondBalance = p.lift(1).get._2
    (firstBalance < 0 && secondBalance > 0) ||
      (firstBalance < 0 && secondBalance < 0)
  }.toList

  val debtPeriods = intermCalc.map { e =>
    val principal = e(0)._2.abs
    val startDate = e(0)._1
    val endDate = Some(e(1)._1.minusDays(1))
    DebtPeriod(principal, startDate, endDate)}

  val lastBalance = intermCalc.last.last._2
  val lastDeptStartDate = intermCalc.last.last._1

  val finalResult =
    if (lastBalance < 0)
      debtPeriods :+ DebtPeriod(lastBalance.abs, lastDeptStartDate, None)
    else
      debtPeriods

  (finalResult)
}

def debtPeriod(account: String) = {

  val operations = Operation.getList

  val result = operations.filter(_.acc_num == account)
    .groupBy( _.date)
    .map {case (date, _) => (date, balanceByDate(account, date))}.toList

  println("result  => " + result)

  //
  /**
    * loop from 0 to list.length - 2
    * if element.balance < 0 //starts debt period
    *   DebtPeriod(element.date, element(+1).date, element.date)
    */

}

