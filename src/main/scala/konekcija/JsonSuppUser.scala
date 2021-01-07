package konekcija

import java.time.LocalDate

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsObject, JsString, JsValue, RootJsonFormat}

  trait JsonSuppUser extends SprayJsonSupport with DefaultJsonProtocol {
    implicit object UserJsonFormat extends RootJsonFormat[User] {
      def read(value: JsValue): User = {
        value.asJsObject.getFields("firstname", "surname", "username", "pass", "registration_date", "update_date") match {
          case Seq(JsString(firstname), JsString(surname), JsString(username), JsString(pass),
          JsString(registration_date), JsString(update_date)) => {
            User(None, firstname, surname, username, pass,
              LocalDate.parse(registration_date, User.dateFormater).atStartOfDay(), LocalDate.parse(update_date, User.dateFormater).atStartOfDay())
          }
          case _ => throw new DeserializationException("No user found")
        }
      }

      def write(v: User): JsObject = {
        val id: Long = v.id.getOrElse(0L)

        JsObject(
          "id" -> JsNumber(id),
          "firstname" -> JsString(v.firstname),
          "surname" -> JsString(v.surname),
          "username" -> JsString(v.username),
          "pass" -> JsString(v.pass),
          "registration_date" -> JsString(v.registration_date.format(User.dateFormater)),
          "update_date" -> JsString(v.update_date.format(User.dateFormater))
        )
      }
    }
  }

