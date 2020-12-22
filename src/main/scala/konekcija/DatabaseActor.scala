package konekcija

import java.time.LocalDateTime

import akka.actor.Actor
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import konekcija.DatabaseActor.{AddVehicle, DeleteVehicle, EditVehicle, ListVehicles, MostCommonBrand, SearchVehicle}
import slick.jdbc.PostgresProfile.api._

object DatabaseActor {
  case class ListVehicles(plate: Option[String])
  case class DeleteVehicle(id: Long)
  case class EditVehicle(id: Long, id_company: Long, brand: String, model: String, plate: String, category: String, registration_date: LocalDateTime,
                         registration_end_date: LocalDateTime)
  case class AddVehicle(vehicle: Vehicle)
  case class SearchVehicle(searchTerm: Option[String])
  case class MostCommonBrand()
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

    case ListVehicles(plate) => {
      listVehicles(plate)(connection).pipeTo(sender)
    }
    case EditVehicle(id, id_company, brand, model, plate, category, registration_date, registration_end_date) => {
      editVehicle(id, id_company, brand, model, plate, category, registration_date, registration_end_date)(connection).pipeTo(sender)
    }
    case SearchVehicle(searchTerm) => {
      searchVehicle(searchTerm)(connection).pipeTo(sender)
    }
    case DeleteVehicle (id) => {
      deleteVehicle(id)(connection).pipeTo(sender)
    }
    case AddVehicle(vehicle: Vehicle) => {
      addVehicle(vehicle)(connection).pipeTo(sender)
    }
    case MostCommonBrand() => {
      sender ! mostCommonBrand()(connection)
    }
  }
}
