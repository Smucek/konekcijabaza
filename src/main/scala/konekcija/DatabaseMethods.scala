package konekcija

import java.io.{File, FileWriter}
import java.time.LocalDateTime

import slick.jdbc.PostgresProfile.api._
import spray.json.{JsObject, JsString}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait DatabaseMethods {

  import scala.concurrent.ExecutionContext.Implicits.global

  def getUser(username: String, pass: String)(connection: Database): Future[User] = {

//        val userRequest = username match {
//          case Some(userFound: String) =>
//            sql"select * from users where username = '#${userFound}' and pass = '#${pass.get}';".as[User]
//            case _ => null
//        }
//        connection.run(userRequest)

    val userRequest = sql"select * from users where username = '#${username}' and pass = '#${pass}';".as[User]
    connection.run(userRequest.headOption).flatMap { result =>
      result match {
        case Some(user) => Future.successful(user)
        case _ => Future.failed(new Throwable("User not found"))
      }
    }
  }

  def listVehicles(plate: Option[String])(connection: Database): Future[Seq[Vehicle]] = {
    //    val vehicleRequest = sql"select * from vehicles".as[Vehicle]

    val vehicleRequest = plate match {
      case Some(vehiclePlate: String) =>
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
      case Some(term: String) => sql"select * from vehicles where is_deleted = false AND (lower(brand) LIKE '%#${term.toLowerCase}%' or lower(model) LIKE '%#${term.toLowerCase}%' or lower(plate) LIKE '%#${term.toLowerCase}%');".as[Vehicle]
      case None => sql"select * from vehicles".as[Vehicle]
    }
    connection.run(vehicleSearch)
  }

  def deleteVehicle(id: Long)(connection: Database): Future[Vehicle] = {

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

  //most common brand as a function in program
//  def mostCommonBrand()(connection: Database): Unit = {
//    val brandRequest = sql"select brand from vehicles".as[(String)]
//
//    connection.run(brandRequest).onComplete {
//      case Success(brands) => brands
//
//          println(brands)
//          var brandSorted: Seq[String] = brands.sorted
//          var counter = 0
//          var mostCommonBrand: String = ""
//
//          while (counter < brandSorted.size) {
//            val brandDrop: Seq[String] = brandSorted.dropWhile(_ == brandSorted.head)
//            if ((brandSorted.size - brandDrop.size) > counter) {
//              mostCommonBrand = brandSorted.head
//              counter = brandSorted.size - brandDrop.size
//              brandSorted = brandDrop
//            }
//            else {
//              brandSorted = brandDrop
//            }
//          }
//
//          val file = new File("test.json")
//          val writer = new FileWriter(file, false)
//
//          writer.append(s"""{\n "brand": "${mostCommonBrand}",\n "count": "${counter.toString}"\n}""")
//          writer.close()
//
//          println(mostCommonBrand)
//          println(counter)
//
//      case Failure(ex) => {
//          println(s"failure: ${ex.getMessage}")
//          ex.printStackTrace()
//      }
//    }
//  }

  def mostCommonBrand()(connection: Database): Unit = {
    val brandRequest = sql"select brand, count(brand) as counter from vehicles where is_deleted = false group by brand order by counter desc limit 5".as[(String)]

    connection.run(brandRequest).onComplete {
      case Success(brands) => brands

        println(brands)

          val file = new File("test.json")
          val writer = new FileWriter(file, false)

          val brand = JsObject(
            "first" -> JsString(brands(0)),
            "second" -> JsString(brands(1)),
            "third" -> JsString(brands(2)),
            "fourth" -> JsString(brands(3)),
            "fifth" -> JsString(brands(4))
          )
          writer.append(brand.toString)
          writer.close()

      case Failure(ex) => {
        println(s"failure: ${ex.getMessage}")
        ex.printStackTrace()
      }
    }
  }
}