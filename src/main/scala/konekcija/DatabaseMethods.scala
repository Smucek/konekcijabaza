package konekcija

import java.time.LocalDateTime

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

trait DatabaseMethods {
  import scala.concurrent.ExecutionContext.Implicits.global

  def listVehicles(plate: Option[String])(connection: Database): Future[Seq[Vehicle]] = {
//    val vehicleRequest = sql"select * from vehicles".as[Vehicle]

    val vehicleRequest = plate match {
      case Some (vehiclePlate: String) =>
        sql"select * from vehicles where is_deleted = false AND plate LIKE '%#${vehiclePlate}%';".as[Vehicle]
      case None => sql"select * from vehicles where is_deleted = false".as[Vehicle]
    }

    connection.run(vehicleRequest)
  }

  def editVehicle(id: Long, id_company: Long, brand: String, model: String, plate: String, category: String,
                  registration_date: LocalDateTime, registration_end_date: LocalDateTime)(connection: Database): Future[Vehicle] = {
    val vehicle = Vehicle(Some(id), Some(id_company), brand, model, plate, category,
      registration_date, registration_end_date, null, null, false)

    val update =
      sql"""
           update vehicles
           set brand = '#${brand}',
               model = '#${model}',
               plate = '#${plate}',
               category = '#${category}',
               registration_date = '#${registration_date}',
               registration_end_date = '#${registration_end_date}'
           where id = '#${id}'
           returning vehicles.*;
         """.as[Vehicle]

    connection.run(update).flatMap { result =>
      result.headOption match {
        case Some(vehicle) => Future.successful(vehicle)
        case None => Future.failed(new Throwable("Can't find vehicle"))
      }
    }
  }

  def searchVehicle(searchTerm: Option[String])(connection: Database): Future[Seq[Vehicle]] = {

    val vehicleSearch = searchTerm match {
      case Some(term: String)  => sql"select * from vehicles where is_deleted = false AND (lower(brand) LIKE '%#${term.toLowerCase}%' or lower(model) LIKE '%#${term.toLowerCase}%' or lower(plate) LIKE '%#${term.toLowerCase}%');".as[Vehicle]
      case None => sql"select * from vehicles".as[Vehicle]
    }
    connection.run(vehicleSearch)
  }

  def deleteVehicle (id: Long)(connection: Database): Future[Vehicle] = {

    val delete =
      sql"""
           update vehicles
           set is_deleted = true
           where id = '#${id}'
           returning vehicles.*;
         """.as[Vehicle]

    connection.run(delete).flatMap { result =>
      result.headOption match {
        case Some(vehicle) => Future.successful(vehicle)
        case None => Future.failed(new Throwable("Vehicle not deleted!"))
      }
    }
  }

  def addVehicle(vehicle: Vehicle)(connection: Database): Future[Vehicle] = {

    val insert =
      sql"""
           insert into vehicles (id_company, brand, model, plate, category, registration_date, registration_end_date)
           values (null, '#${vehicle.brand}', '#${vehicle.model}', '#${vehicle.plate}', '#${vehicle.category}', '#${vehicle.registration_date.toString}', '#${vehicle.registration_end_date.toString}')
           returning vehicles.*;
         """.as[Vehicle]

    connection.run(insert).map { result =>
      result.head
    }
  }
}
