package konekcija

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import slick.jdbc.GetResult

case class Vehicle(
                    id: Option[Long],
                    id_company: Option[Long],
                    brand: String,
                    model: String,
                    plate: String,
                    category: String,
                    registration_date: LocalDateTime,
                    registration_end_date: LocalDateTime,
                    creation_date: LocalDateTime,
                    update_date: LocalDateTime
                  )

object Vehicle {

  val dateFormater = DateTimeFormatter.ofPattern("dd.MM.YYYY")

implicit val vehicleResult = GetResult[Vehicle]({ r =>
    val rs = r.rs

    Vehicle(
      Some(rs.getLong("id")),
      Some(rs.getLong("id_company")),
      rs.getString("brand"),
      rs.getString("model"),
      rs.getString("plate"),
      rs.getString("category"),
      rs.getTimestamp("registration_date").toLocalDateTime,
      rs.getTimestamp("registration_end_date").toLocalDateTime,
      rs.getTimestamp("creation_date").toLocalDateTime,
      rs.getTimestamp("update_date").toLocalDateTime)
  })
}
