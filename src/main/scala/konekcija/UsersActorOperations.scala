package konekcija

import akka.actor.Actor
import akka.actor.Status.{Failure, Success}
import akka.http.scaladsl.model.headers.Connection
import konekcija.UsersActorOperations.ListUsers

object UsersActorOperations {

  case class CreateUser()
  case class DeleteUser()
  case class UpdateUser()
  case class ReadUser()
  case class ListUsers()
}

  class UsersActorOperations extends Actor {
    override def receive: Receive = {

      case ListUsers => {
        def listUsers (connection: Connection) = {
          val usersRequest = sql"select firstname, surname, registration_date from users".as[(String, String, String)]

          connection.run(usersRequest).onComplete {
            case Success(r) => println(s"Result: ${r}")
            case Failure(ex) => {
              println(s"failure: ${ex.getMessage}")
              ex.printStackTrace()
            }
          }
        }
        sender ! listUsers
      }
    }
}
