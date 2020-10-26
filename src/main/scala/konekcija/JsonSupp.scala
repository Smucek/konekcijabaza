package konekcija

import java.time.LocalDateTime

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit object VehicleJsonFormat extends RootJsonFormat[Vehicle] {
    def read(value: JsValue): Vehicle = {
      value.asJsObject.getFields("brand", "model", "plate", "category") match {
        case Seq(JsString(brand), JsString(model), JsString(plate), JsString(category)) => {
          Vehicle(None, brand, model, plate, category, registration_date = LocalDateTime.now(), registration_end_date = LocalDateTime.now.plusYears(1))
        }
        case _ => throw new DeserializationException("Wrong inputs")
      }
    }

    def write(v: Vehicle): JsObject = {
      val id: Long = v.id.getOrElse(0L)
      JsObject(
        "id" -> JsNumber(id),
        "brand" -> JsString(v.brand),
        "model" -> JsString(v.model),
        "plate" -> JsString(v.plate),
        "category" -> JsString(v.category),
        "registration_date" -> JsString(v.registration_date.format(Vehicle.dateFormater)),
        "registration_end_date" -> JsString(v.registration_end_date.format(Vehicle.dateFormater))
      )
    }
  }
}


