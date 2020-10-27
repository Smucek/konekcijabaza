package konekcija

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.util.Timeout

import scala.concurrent.duration._

object FirstApi extends App with Directives with JsonSupport {
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val as = ActorSystem()
  val dbActor = as.actorOf(Props(new UsersActorOperations))
  implicit val timeout = Timeout(3.seconds)


  val getUser = get {
    path("users" / Segment) {id =>
      complete {
        "Searched user " + id
      }
    }
  }

  val getVehicles = get {
    path("vehicles" / Segment) {id =>
      complete {
        "Searched vehicle " + id
      }
    }
  }

  val addVehicle = post {
    path("vehicles") {
      entity(as[Vehicle]) { vehicle =>
        complete {
          vehicle
        }
      }
    }
  }


  val routes = getUser ~ getVehicles ~ addVehicle

  val httpCtx = Http()
  httpCtx.bindAndHandle(routes,"localhost", 8090)

}
