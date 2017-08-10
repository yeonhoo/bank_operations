package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter


case class Operation(opType: String, accNum: String, description: String, amount: Double, date: LocalDate)
case class Account(accNum: String, balance: Double)

case class SimpleInfo(description: String, amount: Double)
case class SimpleInfoByDate(date: LocalDate, info: List[SimpleInfo], balance: Double)

object Account {

  var account1 = Account("12345", 5000)
  var account2 = Account("34567", 10000)
  var account3 = Account("99999", 80000)
  var account4 = Account("77777", 1000000)

  var list: List[Account] = {
    List(
      account1,account2,account3,account4
    )
  }
  def getAccounts = list
  def credit(accountNumber: String, amount: Double) = {}
  def debit(accountNumber: String, amount: Double) = {}
}


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
