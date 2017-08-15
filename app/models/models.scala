package models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import play.api.libs.functional.syntax.unlift
import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

case class Operation(opType: String, accNum: String, description: String, amount: BigDecimal, date: LocalDate)
case class SimpleInfo(description: String, amount: BigDecimal)
case class SimpleInfoByDate(date: LocalDate, info: List[SimpleInfo], balance: BigDecimal)
case class DebtPeriod(start: LocalDate, end: Option[LocalDate], principal: BigDecimal)

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
    (JsPath \ "opType").write[String] and
      (JsPath \ "account").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "amount").write[BigDecimal] and
      (JsPath \ "date").write[LocalDate]
    )(unlift(Operation.unapply))

  implicit val debtPeriodWrites: Writes[DebtPeriod] = (
    (JsPath \ "start").write[LocalDate] and
      (JsPath \ "end").write[Option[LocalDate]] and
      (JsPath \ "principal").write[BigDecimal]
    )(unlift(DebtPeriod.unapply))

  implicit val operationReads: Reads[Operation] = (
    (JsPath \ "opType").read[String] and
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
    (JsPath \ "start").read[LocalDate] and
      (JsPath \ "end").readNullable[LocalDate] and
      (JsPath \ "principal").read[BigDecimal]
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
