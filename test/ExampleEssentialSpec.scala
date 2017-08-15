
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.libs.json.Reads._
import play.api.test.FakeRequest
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import models._
import models.Converters._


class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest {

  "HomeController" should {

    "add new Credit operation and save" in {
      val request = FakeRequest("POST", "/operation").withJsonBody(
        Json.parse(
          """
            |{"op_type": "Credit",
            |"account": "12345",
            |"description": "deposit",
            |"amount": 200,
            |"date": "2017-10-17"}
          """.stripMargin))

      println(request.body)

      val apiResult = route(app, request).get

      val jsonBody = contentAsJson(apiResult)
      println((jsonBody \ "message").as[String])
      (jsonBody \ "status").as[String] mustBe ("Ok")
      (jsonBody \ "message").as[String] must include("operation : Credit")
      (jsonBody \ "message").as[String] must include("description : deposit")
      (jsonBody \ "message").as[String] must include("amount : 200")
      (jsonBody \ "message").as[String] must include("date : 2017-10-17")

    }



    "return balance for account 12345" in {
      val request = FakeRequest(GET, "/balance/12345").withHeaders(HOST -> "localhost:9000",
        "Content-type" -> "application/json")
      val balance = route(app, request).get

      val jsonBody = contentAsJson(balance)

      jsonBody.toString() mustEqual ("-528.57")
      //contentAsString(balance) must include ("1234")
    }


    "return statement for account 12345 from 15/07/2017 to 17/07/2017" in {

      val request = FakeRequest(GET, "/statement/12345/15102017/17102017").withHeaders(HOST -> "localhost:9000",
        "Content-type" -> "application/json")
      val apiResult = route(app, request).get


      val validated = contentAsJson(apiResult).validate[List[SimpleInfoByDate]]

      validated.isSuccess mustBe true

      val dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy")

      val firstElement = validated.get.lift(0).get

      firstElement.date mustBe LocalDate.parse("15102017", dateFormat)
      firstElement.balance mustBe 1000
      firstElement.info mustBe List(SimpleInfo("Deposit", 1000))

      val secondElement = validated.get.lift(1).get
      secondElement.date mustBe LocalDate.parse("16102017", dateFormat)
      secondElement.balance mustBe 951.43
      secondElement.info mustBe List(
        SimpleInfo("Purchase on Amazon", 3.34),
        SimpleInfo("Purchase on Uber", 45.23)
      )

      val thirdElement = validated.get.lift(2).get
      thirdElement.date mustBe LocalDate.parse("17102017", dateFormat)
      thirdElement.balance mustBe 771.43
      thirdElement.info mustBe List(
        SimpleInfo("Withdrawal", 180)
      )
    }

    "return debit period for account 12345" in {

      val request = FakeRequest(GET, "/debt/12345").withHeaders(HOST -> "localhost:9000",
        "Content-type" -> "application/json")
      val apiResult = route(app, request).get
      val validated = contentAsJson(apiResult).validate[List[DebtPeriod]]

      validated.isSuccess mustBe true

      val dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy")

      val firstElement = validated.get.lift(0).get

      firstElement mustBe DebtPeriod(
        -28.57,
        LocalDate.parse("18102017", dateFormat),
        Some(LocalDate.parse("24102017", dateFormat)))

      val secondElement = validated.get.lift(1).get

      secondElement mustBe DebtPeriod(
        -228.57,
        LocalDate.parse("30102017", dateFormat),
        Some(LocalDate.parse("02112017", dateFormat)))

      val thirdElement = validated.get.lift(2).get

      thirdElement mustBe DebtPeriod(
        -628.57,
        LocalDate.parse("03112017", dateFormat),
        Some(LocalDate.parse("04112017", dateFormat)))

      val fourthElement = validated.get.lift(3).get

      fourthElement mustBe DebtPeriod(
        -528.57,
        LocalDate.parse("05112017", dateFormat),
        None)
    }
  }
}