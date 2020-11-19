package konekcija

import java.time.{LocalDate, LocalDateTime}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

import scala.collection.immutable.Range


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object VehicleJsonFormat extends RootJsonFormat[Vehicle] {
    def read(value: JsValue): Vehicle = {
      value.asJsObject.getFields("brand", "model", "plate", "category", "registration_date", "registration_end_date") match {
        case Seq(JsString(brand), JsString(model), JsString(plate), JsString(category),
        JsString(registration_date), JsString(registration_end_date)) => {
          Vehicle(None, None, brand, model, plate, category,
            LocalDate.parse(registration_date, Vehicle.dateFormater).atStartOfDay(), LocalDate.parse(registration_end_date, Vehicle.dateFormater).atStartOfDay(),
            LocalDateTime.now(), LocalDateTime.now())
        }
        case _ => throw new DeserializationException("Wrong inputs")
      }
    }

    def write(v: Vehicle): JsObject = {
      val id: Long = v.id.getOrElse(0L)
      val id_company: Long = v.id.getOrElse(1L)

      JsObject(
        "id" -> JsNumber(id),
        "id_company" -> JsNumber(id_company),
        "brand" -> JsString(v.brand),
        "model" -> JsString(v.model),
        "plate" -> JsString(v.plate),
        "category" -> JsString(v.category),
        "registration_date" -> JsString(v.registration_date.format(Vehicle.dateFormater)),
        "registration_end_date" -> JsString(v.registration_end_date.format(Vehicle.dateFormater)),
        "creation_date" -> JsString(v.creation_date.format(Vehicle.dateFormater)),
        "update_date" -> JsString(v.update_date.format(Vehicle.dateFormater))
      )
    }
  }
}


