package konekcija

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives

import scala.util.parsing.json.JSONObject

object FirstApi extends App with Directives{
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val as = ActorSystem()

  val getUser = get {
    path("users" / Segment) {id =>
      complete {
        "Searched user " + id
      }
    }
  }

  val getVehicles = get {
    path("vehicles") {
      parameters('plate.as[String].?, 'registrtion_date.as[String].?) { (plate, registration_date) =>
        complete {
          val res = Map(
            "brand" -> plate,
            "registration_date" -> registration_date
          )
            HttpResponse(
              StatusCodes.OK,
              entity = HttpEntity(
                ContentTypes.`application/json`,
                (new JSONObject(res)).toString().getBytes()
              )
            )
            //        s"vehicle ${brand}, with date ${registration_date}, ${attributes.mkString(", ")}"
          }
        }
      }
    }


  val routes = getUser ~ getVehicles

  val httpCtx = Http()
  httpCtx.bindAndHandle(routes,"localhost", 8090)

}
