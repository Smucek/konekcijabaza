package konekcija

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import slick.jdbc.GetResult

case class User(
                 id: Option[Long],
                 firstname: String,
                 surname: String,
                 username: String,
                 pass: String,
                 registration_date: LocalDateTime,
                 update_date: LocalDateTime
               )

object User {

  val dateFormater = DateTimeFormatter.ofPattern("dd.MM.yyyy")
  //  val dateFormater = new SimpleDateFormat("dd.MM.YYYY")


  implicit val userResult = GetResult[User]({ r =>
    val rs = r.rs

    User(
      Some(rs.getLong("id")),
      rs.getString("firstname"),
      rs.getString("surname"),
      rs.getString("username"),
      rs.getString("pass"),
      rs.getTimestamp("registration_date").toLocalDateTime,
      rs.getTimestamp("update_date").toLocalDateTime
    )
  })
}