package models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Operation(op_type: String, acc_num: String, description: String, amount: BigDecimal, date: LocalDate)
case class SimpleInfo(description: String, amount: BigDecimal)
case class SimpleInfoByDate(date: LocalDate, info: List[SimpleInfo], balance: BigDecimal)
case class DebtPeriod(principal: BigDecimal, start: LocalDate, end: Option[LocalDate])

object Converters {

  implicit val simpleInfo: Writes[SimpleInfo] = (
    (JsPath \ "description").write[String] and
      (JsPath \ "amount").write[BigDecimal]
    )(unlift(SimpleInfo.unapply))

  implicit val simpleInfobyDateWrites: Writes[SimpleInfoByDate] = (
    (JsPath \ "date").write[LocalDate] and
      (JsPath \ "info").write[List[SimpleInfo]] and
      (JsPath \ "balance").write[BigDecimal]
    )(unlift(SimpleInfoByDate.unapply))

  implicit val operationWrites: Writes[Operation] = (
    (JsPath \ "op_type").write[String] and
      (JsPath \ "account").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "amount").write[BigDecimal] and
      (JsPath \ "date").write[LocalDate]
    )(unlift(Operation.unapply))

  implicit val debtPeriodWrites: Writes[DebtPeriod] = (
    (JsPath \ "principal").write[BigDecimal] and
      (JsPath \ "start").write[LocalDate] and
      (JsPath \ "end").write[Option[LocalDate]]
    )(unlift(DebtPeriod.unapply))

  implicit val operationReads: Reads[Operation] = (
    (JsPath \ "op_type").read[String] and
      (JsPath \ "account").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "amount").read[BigDecimal] and
      (JsPath \ "date").read[LocalDate]
    )(Operation.apply _)

  implicit val simpleInfoReads: Reads[SimpleInfo] = (
    (JsPath \ "description").read[String] and
      (JsPath \ "amount").read[BigDecimal]
    )(SimpleInfo.apply _)

  implicit val statementReads: Reads[SimpleInfoByDate] = (
    (JsPath \ "date").read[LocalDate] and
      (JsPath \ "info").read[List[SimpleInfo]] and
      (JsPath \ "balance").read[BigDecimal]
    )(SimpleInfoByDate.apply _)

  implicit val debtPeriodReads: Reads[DebtPeriod] = (
    (JsPath \ "principal").read[BigDecimal] and
      (JsPath \ "start").read[LocalDate] and
      (JsPath \ "end").readNullable[LocalDate]
    )(DebtPeriod.apply _)
}

object Operation {

  def save(operation: Operation) = this.synchronized {
    // to make it faster prefer '::' and then 'reverse' over ':::'
    //list = list ::: List(operation)
    list = (operation :: list).reverse
  }

  def getList = list
  def getList2 = list2

  private var list: List[Operation] = {
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
        "11111",
        "Purchase on Ebay",
        120.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "11111",
        "Purchase on Amazon",
        200.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "11111",
        "Withdrawal",
        200.00,
        LocalDate.parse("17102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "22222",
        "Purchase on Amazon",
        200.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Credit",
        "22222",
        "Deposit",
        500.00,
        LocalDate.parse("16102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "54321",
        "Purchase on Uber",
        25.65,
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
        "Withdrawal",
        300.00,
        LocalDate.parse("30102017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),

      Operation(
        "Debit",
        "12345",
        "Withdrawal",
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

  private var list2: List[Operation] = {
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
        "Debit",
        "12345",
        "transferencia",
        2000.00,
        LocalDate.parse("25072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "transferencia",
        2000.00,
        LocalDate.parse("27072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "transferencia",
        15000.00,
        LocalDate.parse("29072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "transferencia",
        15000.00,
        LocalDate.parse("29072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      ),
      Operation(
        "Debit",
        "12345",
        "transferencia",
        15000.00,
        LocalDate.parse("29072017", DateTimeFormatter.ofPattern("ddMMyyyy"))
      )
    )
  }
}
