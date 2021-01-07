package konekcija

import java.time.LocalDate
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import konekcija.DatabaseActor._
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtSprayJson}
import spray.json.{JsObject, JsString}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object FirstApi extends App with Directives with JsonSupport with JsonSuppUser {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val as = ActorSystem()
  val dbActor = as.actorOf(Props(new DatabaseActor))
  implicit val timeout = Timeout(3.seconds)


  val algorithm = JwtAlgorithm.HS256
  val secretKey = "imamjosmnogouciti"

def createToken(username: Option[String], expiration: Int): String = {
  val claims = JwtClaim(
    expiration = Some(System.currentTimeMillis()/1000 + TimeUnit.DAYS.toSeconds(expiration)),
    issuedAt = Some(System.currentTimeMillis()/1000),
    issuer = Some("ajdin"),
    content = "scalarules"
  )
  JwtSprayJson.encode(claims, secretKey, algorithm)
}

  val getUser = get {
    path("getUser") {

      parameters("username".?, "pass".?) { (username, pass) =>

        val userConfirmed = (dbActor ? GetUser(username, pass)).map(_.asInstanceOf[Seq[User]])
        onComplete(userConfirmed) match {
          case user => {
            val token = createToken(username, 1)
            respondWithHeader(RawHeader("Access-Granted", token)) {
              complete(StatusCodes.OK)
            }
          }
          case _ => complete(StatusCodes.Forbidden)
        }
      }
    }
  }

  val getVehicles = get {
    path("vehicles") {

      parameters("plate".?) { plate =>

      val vehiclesShow = (dbActor ? ListVehicles(plate)).map(_.asInstanceOf[Seq[Vehicle]])
      onComplete(vehiclesShow) { vehicles =>
//        respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
          complete {
            vehicles
          }
        }
      }
    }
  }

  val searchVehicle = get {
    path("vehicle") {
      parameters("searchTerm".?) { searchTerm =>

        val vehiclesShow = (dbActor ? SearchVehicle(searchTerm)).map(_.asInstanceOf[Seq[Vehicle]])
        onComplete(vehiclesShow) { vehicles =>
          //        respondWithHeader(RawHeader("Access-Control-Allow-Origin", "*")) {
          complete {
            vehicles
          }
        }
      }
    }
  }

  val addVehicle = post {
    path("vehicleAdd") {
      entity(as[Vehicle]) { vehicle =>
        val vehicleToAdd = (dbActor ? AddVehicle(vehicle)).map(_.asInstanceOf[Vehicle])
        onComplete(vehicleToAdd) {
          case Success(vehicle) => complete {
            vehicle
          }
          case Failure(ex) => complete {
            HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, ex.getMessage))
          }
        }
      }
    }
  }

  val editVehicle = put {
    path("vehicleEdit" / LongNumber) { id =>
      entity(as[JsObject]) { vehicle =>
        val fields = vehicle.fields

        (fields.get("brand"), fields.get("model"), fields.get("plate"), fields.get("category"), fields.get("registration_date"), fields.get("registration_end_date")) match {
          case (Some(JsString(brand: String)), Some(JsString(model: String)), Some(JsString(plate: String)), Some(JsString(category: String)),
        Some(JsString(registration_date: String)), Some(JsString(registration_end_date: String))) => {
        val edited = (dbActor ? EditVehicle(id, 1L, brand, model, plate, category,
          LocalDate.parse(registration_date, Vehicle.dateFormater).atStartOfDay(), LocalDate.parse(registration_end_date, Vehicle.dateFormater).atStartOfDay())).map(_.asInstanceOf[Vehicle] )

          onComplete (edited) {
             case Success(vehicle) => complete(vehicle)
             case Failure(ex) => complete {
                              HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, ex.getMessage))
              }
          }
        }
//          case (_, _, _, _, _, _,) => complete {
//            HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Wrong inputs."))
//          }
        }
      }
    }
  }




  val deleteVehicle = delete {
    path("vehicleDel" / LongNumber) { id =>
      val vehicleToDelete = (dbActor ? DeleteVehicle(id)).map(_.asInstanceOf[Vehicle])

      onComplete(vehicleToDelete) {
        case Success(vehicle) => complete {vehicle}
        case Failure(ex) => complete {
          HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, ex.getMessage))
        }
      }
    }
  }

  val brands = get {
    path("mostCommonBrand") {
      getFromFile("test.json")
    }
  }


  val routes = getUser ~ getVehicles ~ addVehicle ~ searchVehicle ~ deleteVehicle ~ brands ~ editVehicle

  val httpCtx = Http()
  httpCtx.bindAndHandle(routes,"localhost", 8090)

}
