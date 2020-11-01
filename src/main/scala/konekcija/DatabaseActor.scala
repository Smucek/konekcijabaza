package konekcija

import java.time.LocalDateTime

import akka.actor.Actor
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import konekcija.DatabaseActor.{DeleteVehicle, EditVehicle, ListVehicles, SearchVehicle}
import slick.jdbc.PostgresProfile.api._

object DatabaseActor {
  case class ListVehicles()
  case class DeleteVehicle(id: Long)
  case class EditVehicle(id: Long, id_company: Long, brand: String, model: String, plate: String, category: String, registration_date: LocalDateTime,
                         registration_end_date: LocalDateTime, creation_date: LocalDateTime, update_date: LocalDateTime)
  case class AddVehicle(id: Long, brand: String, model: String, plate: String, category: String, registration_date: LocalDateTime,
                         registration_end_date: LocalDateTime)
  case class SearchVehicle(id: Long, brand: String, model: String, plate: String)
}

class  DatabaseActor extends Actor with DatabaseMethods {
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
      listVehicles()(connection).pipeTo(sender)
    }
    case EditVehicle(id, id_company, brand, model, plate, category, registration_date, registration_end_date, creation_date, update_date) => {
      editVehicle(id, id_company, brand, model, plate, category, registration_date, registration_end_date, creation_date, update_date)(connection).pipeTo(sender)
    }
    case SearchVehicle(id, brand, model, plate) => {
      searchVehicle(id, brand, model, plate)(connection).pipeTo(sender)
    }
    case DeleteVehicle (id) => {
      deleteVehicle(id)(connection).pipeTo(sender)
    }

  }
}
