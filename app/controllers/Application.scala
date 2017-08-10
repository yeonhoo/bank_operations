package controllers

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.google.inject.Inject
import play.api._
import play.api.mvc._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


class HomeController @Inject()(cc:ControllerComponents) extends AbstractController(cc)  {


  implicit val simpleInfo: Writes[SimpleInfo] = (
    (JsPath \ "description").write[String] and
      (JsPath \ "amount").write[Double]
    )(unlift(SimpleInfo.unapply))


  implicit val simpleInfobyDateWrites: Writes[SimpleInfoByDate] = (
    (JsPath \ "date").write[LocalDate] and
      (JsPath \ "info").write[List[SimpleInfo]] and
      (JsPath \ "balance").write[Double]
    )(unlift(SimpleInfoByDate.unapply))

  implicit val operationWrites: Writes[Operation] = (
    (JsPath \ "opType").write[String] and
      (JsPath \ "account").write[String] and
      (JsPath \ "description").write[String] and
      (JsPath \ "amount").write[Double] and
      (JsPath \ "date").write[LocalDate]
    )(unlift(Operation.unapply))



  implicit val accountReads: Reads[Account] = (
    (JsPath \ "account").read[String] and
      (JsPath \ "balance").read[Double]
  )(Account.apply _)

  implicit val operationReads: Reads[Operation] = (
    (JsPath \ "opType").read[String] and
      (JsPath \ "account").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "amount").read[Double] and
      (JsPath \ "date").read[LocalDate]
  )(Operation.apply _)

/*  def saveOperation = Action(parse.json) { request =>
    val operationResult = request.body.validate[Operation]
    operationResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toJson(errors)))
      },
      operation => {
        Operation.save(operation)
        Ok(Json.obj("status" -> "Ok", "message" -> ("Operation :" + operation.opType + ", description :" +
          operation.description + " saved.")))
      }
    )
  }*/

  def listOperations = Action {
    val json = Json.toJson(Operation.list)
    Ok(json)
  }

  def balance(account: String) = Action { request =>

    val operations = Operation.list

    println(operations)

    val calcBalance = operations.filter(_.accNum == account).foldLeft(0.0){ (balance, operation) =>
      val amount = operation.opType match {
        case "Credit" => operation.amount
        case "Debit" => -(operation.amount)
      }
      println("Date : " + operation.date + " actual balance : " + balance + " amount to sum : " + amount )
      balance + amount
    }

    Ok(Json.toJson(calcBalance))
  }


  def balanceByDate(account: String, dateStr: String) = Action { request =>
    val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("ddMMyyyy"))
    val operations = Operation.list

    val computedBalance = operations.filter{ op =>
      op.accNum == account && op.date.compareTo(date) <= 0
    }.foldLeft(0.0) { (balance, operation) =>
      val amount = operation.opType match {
        case "Credit" => operation.amount
        case "Debit" => -(operation.amount)
      }
      println("Date : " + operation.date + " actual balance : " + balance + " amount to sum : " + amount )
      balance + amount
    }

    Ok(Json.toJson(computedBalance))
  }

  def balanceByDate2(account: String, date: LocalDate) = {
    val operations = Operation.list

    val computedBalance = operations.filter{ op =>
      op.accNum == account && op.date.compareTo(date) <= 0
    }.foldLeft(0.0) { (balance, operation) =>
      val amount = operation.opType match {
        case "Credit" => operation.amount
        case "Debit" => -(operation.amount)
      }
      println("Date : " + operation.date + " actual balance : " + balance + " amount to sum : " + amount )
      balance + amount
    }
    computedBalance
  }




  def statement(account: String, from: String, to: String) = Action { request =>
    val operations = Operation.list

    val dateFrom = LocalDate.parse(from, DateTimeFormatter.ofPattern("ddMMyyyy"))
    val dateTo = LocalDate.parse(to, DateTimeFormatter.ofPattern("ddMMyyyy"))

    val data = operations.filter { op =>

      //println("operation date : " + op.date + " dateFrom : " + dateFrom + " compare : " + op.date.compareTo(dateFrom))
      //println("operation date : " + op.date + " dateTo : " + dateTo + " compare : " + op.date.compareTo(dateTo))
      op.accNum == account &&
        op.date.compareTo(dateFrom) >= 0 &&
        op.date.compareTo(dateTo) <= 0
    }.groupBy(_.date).map {
      case (date, xs) => {
        SimpleInfoByDate(date, xs.map(e => SimpleInfo(e.description, e.amount)), balanceByDate2(account, date))
      }
    }.toList.sortWith{ (op1, op2) => op1.date.compareTo(op2.date) <= 0 }

    Ok(Json.toJson(data))
  }





  // if we don't care about validation we could replace `validateJson[Place]`
  // with `BodyParsers.parse.json[Place]` to get an unvalidated case class
  // in `request.body` instead.
/*  def savePlaceConcise = Action(validateJson[Reads[Place]]) { request =>
    // `request.body` contains a fully validated `Place` instance.
    //val place = request.body
    //Place.save(place)

    //Ok(Json.obj("status" ->"OK", "message" -> ("Place '"+place.name+"' saved.") ))

    Ok(Json.obj("status" ->"OK", "message" -> ("Place '"+ "Qual eh o nome " +"' saved.") ))

  }*/

/*  def listPlaces = Action {
    val json = Json.toJson(Place.list)
    Ok(json)
  }*/
}

