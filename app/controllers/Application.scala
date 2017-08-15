package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.inject.Inject
import play.api.mvc._
import play.api.libs.json._
import scala.math.BigDecimal.RoundingMode
import models.Converters._
import models._


class HomeController @Inject()(cc:ControllerComponents) extends AbstractController(cc)  {

  def saveOperation = Action(parse.json) { request =>
    val operationResult = request.body.validate[Operation](operationReads)
    operationResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "Error", "message" -> JsError.toJson(errors)))
      },
      operation => {
        Operation.save(operation)
        Ok(Json.obj("status" -> "Ok", "message" -> ("operation : " + operation.op_type +
          ", description : " + operation.description +
          ", amount : " + operation.amount +
          ", date : " + operation.date + " saved." )))
      }
    )
  }

  val dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy")

  def listOperations = Action {
    val json = Json.toJson(Operation.getList)
    Ok(json)
  }

  def balance(account: String) = Action { request =>

    val operations = Operation.getList
    //println(operations)
    val computedBalance = operations.filter(_.acc_num == account).foldLeft(BigDecimal(0)){ (balance, operation) =>
      val amount = operation.op_type match {
        case "Credit" => operation.amount
        case "Debit" => -(operation.amount)
      }
      //println("Date : " + operation.date + " actual balance : " + balance + " amount to sum : " + amount )
      balance + amount
    }
    Ok(Json.toJson(computedBalance))
  }

  def balanceByDate(account: String, date: LocalDate) = {
    val operations = Operation.getList

    val computedBalance = operations.filter{ op =>
      op.acc_num == account && op.date.compareTo(date) <= 0
    }.foldLeft(BigDecimal(0)) { (balance, operation) =>
      val amount = operation.op_type match {
        case "Credit" => operation.amount
        case "Debit" => -(operation.amount)
      }
      //println("Date : " + operation.date + " actual balance : " + balance + " amount to sum : " + amount )
      balance + amount
    }
    computedBalance.setScale(2, RoundingMode.HALF_EVEN)
  }


  def statement(account: String, from: String, to: String) = Action {
    val operations = Operation.getList
    val dateFrom = LocalDate.parse(from, dateFormat)
    val dateTo = LocalDate.parse(to, dateFormat)

    val data = operations.filter { op =>
      op.acc_num == account && op.date.compareTo(dateFrom) >= 0 && op.date.compareTo(dateTo) <= 0
    }.groupBy(_.date).map {
      case (date, xs) => {
        SimpleInfoByDate(date, xs.map(e => SimpleInfo(e.description, e.amount.setScale(2, RoundingMode.HALF_EVEN))), balanceByDate(account, date))
      }
    }.toList.sortWith{ (op1, op2) => op1.date.compareTo(op2.date) <= 0 }

    Ok(Json.toJson(data))
  }


  def debtPeriod2(account: String) = Action {
    val operations = Operation.getList

    val result = operations.filter(_.acc_num == account)
      .groupBy( op => op.date)
      .map {case (date, _) => (date, balanceByDate(account, date))}
      .toList.sortWith( (date1, date2) => date1._1.compareTo(date2._1) <= 0)
      .sliding(2).filter{ p=>
        println("p.list 0 => " + p.lift(0) + "p.list 1 => " + p.lift(1))
        println("p.list 0 => " + p.lift(0) + "p.list 1 getOrElse => " + p.lift(1))

      val firstBalance = p.lift(0).get._2
        val secondBalance = p.lift(1).getOrElse(p.lift(0).get)._2
        (firstBalance < 0 && secondBalance > 0) ||
        (firstBalance < 0 && secondBalance < 0)
      }.toList.

    println(result)

    println("first El : " + result.lift(0))
    //println("second El : " + result.lift(0).get.lift(1))


    val debtPeriods = result.map { e =>
      val principal = e(0)._2.abs
      val startDate = e(0)._1
      val endDate = Some(e(1)._1.minusDays(1))
      DebtPeriod(principal, startDate, endDate)}

    val lastBalance = result.last.last._2
    val lastDeptStartDate = result.last.last._1

    val finalResult =
      if (lastBalance < 0)
        debtPeriods :+ DebtPeriod(lastBalance.abs, lastDeptStartDate, None)
      else
        debtPeriods

    Ok(Json.toJson(finalResult))
  }
  def debtPeriod(account: String) = Action {
    val operations = Operation.getList

    val result = operations.filter(_.acc_num == account)
      .groupBy( op => op.date)
      .map {case (date, _) => date}
      .toList.sortWith( (date1, date2) => date1.compareTo(date2) <= 0)
      .map(date => Tuple2(date, balanceByDate(account, date))) // corrigir o nome da funcao balanceByDate
      .sliding(2).filter{ p=>
      //println("primeiro : " + p.lift(0).get._2 + "  Date : " + p.lift(0).get._1)
      //println("segundo : " + p.lift(1).get._2 + "  Date : " + p.lift(1).get._1)
      (p.lift(0).get._2 < 0 && p.lift(1).get._2 > 0) ||
        (p.lift(0).get._2 < 0 && p.lift(1).get._2 < 0)
    }
      .flatten.grouped(2).toList

    val lastBalance = result.last.last
    //println("last balance  mesmo ? : " + lastBalance)

    val finalResult = result.map{m =>
      DebtPeriod(m(0)._2.abs, m(0)._1, Some(m(1)._1.minusDays(1)) ) } :+ DebtPeriod(lastBalance._2.abs, lastBalance._1, None)

    Ok(Json.toJson(finalResult))

  }
}

