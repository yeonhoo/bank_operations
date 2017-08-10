package controllers

import com.google.inject.Inject
import play.api._
import play.api.mvc._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by yeonhoo on 05/08/2017.
  */

class TestController @Inject()(cc:ControllerComponents) extends AbstractController(cc)  {


  def testando = Action { request =>
    Ok("hi")
  }
}

