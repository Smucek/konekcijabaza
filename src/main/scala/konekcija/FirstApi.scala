package konekcija

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import konekcija.DatabaseActor.{DeleteVehicle, ListVehicles, SearchVehicle}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object FirstApi extends App with Directives with JsonSupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val as = ActorSystem()
  val dbActor = as.actorOf(Props(new DatabaseActor))
  implicit val timeout = Timeout(3.seconds)


  val getUser = get {
    path("user" / Segment) {id =>
      complete {
        "Searched user " + id
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

//  complete {
//          HttpResponse(
//            StatusCodes.OK,
//            entity = HttpEntity(
//              ContentTypes.`application/json`,
//              (new JSONObject(res)).toString().getBytes()
//            )
//          )
//        }
//      }
//    }
//  }


  val addVehicle = post {
    path("vehicleAdd") {
      entity(as[Vehicle]) { vehicle =>
        complete {
          vehicle
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

  val forma = get {
    path("forma") {
      getFromFile("VehiclePage.html")
    }
  }


  val routes = getUser ~ getVehicles ~ addVehicle ~ searchVehicle ~ deleteVehicle ~ forma

  val httpCtx = Http()
  httpCtx.bindAndHandle(routes,"localhost", 8090)

}
