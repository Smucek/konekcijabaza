package konekcija

import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile.api._

import scala.util.{Failure, Success}

object GlavniProgram {
  import scala.concurrent.ExecutionContext.Implicits.global
  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()
    val host = config.getString("database.host")
    val port = config.getInt("database.port")
    val dbName = config.getString("database.dbname")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val url = s"jdbc:postgresql://${host}:${port}/${dbName}?ApplicationName=test"
    val connection = Database.forURL(url, username, password, null, "org.postgresql.Driver")

    val usersRequest = sql"select firstname, surname, registration_date from users".as[(String, String, String)]

    connection.run(usersRequest).onComplete {
      case Success(r) => println(s"Result: ${r}")
      case Failure(ex) => {
        println(s"failure: ${ex.getMessage}")
        ex.printStackTrace()
      }
    }
    val vehicleRequest = sql"select brand, model, plate, registration_date, registration_end_date from vehicles".as[Vehicle]

    connection.run(vehicleRequest).onComplete {
      case Success(r) => println(s"Result: ${r}")
      case Failure(ex) => {
        println(s"failure: ${ex.getMessage}")
        ex.printStackTrace()
      }
    }
    Thread.sleep(3000)
  }
}
