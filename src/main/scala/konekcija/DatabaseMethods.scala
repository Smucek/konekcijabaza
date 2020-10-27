package konekcija

import java.time.LocalDateTime

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

trait DatabaseMethods {
  import scala.concurrent.ExecutionContext.Implicits.global

  def listVehicles()(connection: Database): Future[Seq[Vehicle]] = {
    val vehicleRequest = sql"select * from vehicles".as[Vehicle]

    connection.run(vehicleRequest).flatMap { vehicle =>
      Future {
        vehicle
      }
    }
  }

  def editVehicle(id: Long, brand: String, model: String, plate: String,
                    category: String, registration_date: LocalDateTime, registration_end_date: LocalDateTime)(connection: Database): Future[Vehicle] = {
    val vehicle = Vehicle(Some(id), brand, model, plate, category, registration_date, registration_end_date)

    val update =
      sql"""
           update vehicles
           set brand = '#${brand}', model = #${model}, plate = #${plate}, category = #${category}, registration_date = #${registration_date}, registration_end_date = #${registration_end_date}
           where id = #${id}
           returning vehicles.*;
         """.as[Vehicle]

    connection.run(update).flatMap { result =>
      result.headOption match {
        case Some(vehicle) => Future.successful(vehicle)
        case None => Future.failed(new Throwable("Can't find vehicle"))
      }
    }
  }
}
