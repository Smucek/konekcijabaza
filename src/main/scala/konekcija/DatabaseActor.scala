package konekcija

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import konekcija.DatabaseActor.{EditVehicle, ListVehicles}
import slick.jdbc.PostgresProfile.api._

object DatabaseActor {
  case class ListVehicles()
  case class DeleteVehicle(id: Long)
  case class EditVehicle(id: Long, brand: String, model: String, plate: String, category: String, registration_date: LocalDateTime,
                         registration_end_date: LocalDateTime)
}

class DatabaseActor extends Actor with DatabaseMethods {
  import scala.concurrent.ExecutionContext.Implicits.global

  val config = ConfigFactory.load()
  val host = config.getString("database.host")
  val port = config.getInt("database.port")
  val dbName = config.getString("database.dbname")
  val username = config.getString("database.username")
  val password = config.getString("database.password")

  val url = s"jdbc:postgresql://${host}:${port}/${dbName}?ApplicationName=test"
  val connection = Database.forURL(url, username, password, null, "org.postgresql.Driver")


  def receive = {

    case ListVehicles() => {
      listVehicles()(connection).pipeTo(sender
    }

    case EditVehicle(id, brand, model, plate, category, registration_date, registration_end_date) => {
      editVehicle(id, brand, model, plate, category, registration_date, registration_end_date)(connection).pipeTo(sender)
    }
  }
}
