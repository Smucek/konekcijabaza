package konekcija

import java.time.LocalDateTime

import slick.jdbc.GetResult

case class Vehicle(brand: String, model: String, plate: String, registration_date: LocalDateTime, registration_end_date: LocalDateTime)

object Vehicle {
implicit val vehicleResult = GetResult[Vehicle]({ r =>
    val rs = r.rs

    Vehicle(
      rs.getString("brand"),
      rs.getString("model"),
      rs.getString("plate"),
      rs.getTimestamp("registration_date").toLocalDateTime,
      rs.getTimestamp("registration_end_date").toLocalDateTime
    )
  })
}
