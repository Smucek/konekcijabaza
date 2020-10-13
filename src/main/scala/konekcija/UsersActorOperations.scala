package konekcija

import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import konekcija.UsersActorOperations.ListUsers
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object UsersActorOperations {
  case class CreateUser()
  case class DeleteUser()
  case class UpdateUser()
  case class ReadUser()
  case class ListUsers()
}

  class UsersActorOperations extends Actor {

    val config = ConfigFactory.load()
    val host = config.getString("database.host")
    val port = config.getInt("database.port")
    val dbName = config.getString("database.dbname")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val url = s"jdbc:postgresql://${host}:${port}/${dbName}?ApplicationName=test"
    val connection = Database.forURL(url, username, password, null, "org.postgresql.Driver")

    override def receive: Receive = {

      case ListUsers => {
        def listUsers  = {
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
