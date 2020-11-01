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

  def editVehicle(id: Long, id_company: Long, brand: String, model: String, plate: String, category: String,
                  registration_date: LocalDateTime, registration_end_date: LocalDateTime,
                  creation_date: LocalDateTime, update_date: LocalDateTime)(connection: Database): Future[Vehicle] = {
    val vehicle = Vehicle(Some(id), Some(id_company), brand, model, plate, category,
      registration_date, registration_end_date, creation_date, update_date)

    val update =
      sql"""
           update vehicle
           set brand = '#${brand}', model = #${model}, plate = #${plate}, category = #${category},
           registration_date = #${registration_date}, registration_end_date = #${registration_end_date}
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

  def searchVehicle(id: Long, brand: String, model: String, plate: String)(connection: Database): Future[Vehicle] = {
    val vehicleSearch = sql"select brand, model, plate from vehicles".as[Vehicle]

    connection.run(vehicleSearch).flatMap { result =>
      result.headOption match {
        case Some(vehicle) => Future(vehicle)
      }
    }
  }

  def deleteVehicle (id: Long)(connection: Database): Future[Vehicle] = {
    val delete =
      sql"""
           delete vehicle
           where id = #${id}
           returning vehicles.*;
         """.as[Vehicle]

    connection.run(delete).flatMap { result =>
      result.headOption match {
        case Some(vehicle) => Future.successful(vehicle)
        case None => Future.failed(new Throwable("Can't find vehicle"))
      }
    }
  }

  def addVehicle(id: Long, id_company: Long, brand: String, model: String, plate: String, category: String,
                  registration_date: LocalDateTime, registration_end_date: LocalDateTime,
                  creation_date: LocalDateTime, update_date: LocalDateTime)(connection: Database): Future[Vehicle] = {
    val vehicle = Vehicle(Some(id), Some(id_company), brand, model, plate, category,
      registration_date, registration_end_date, creation_date, update_date)

    val update =
      sql"""
           insert vehicle
           set brand = '#${brand}', model = #${model}, plate = #${plate}, category = #${category},
           registration_date = #${registration_date}, registration_end_date = #${registration_end_date}
           where id = #${id}
           returning vehicles.*;
         """.as[Vehicle]

    connection.run(update).flatMap { result =>
      result.headOption match {
        case Some(vehicle) => Future.successful(vehicle)
        case None => Future.failed(new Throwable("Vehicle not added"))
      }
    }
  }
}
