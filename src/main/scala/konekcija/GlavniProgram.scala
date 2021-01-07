package konekcija

import java.io.{File, FileWriter}

import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile.api._

import scala.util.{Failure, Success}

object GlavniProgram {
  import scala.concurrent.ExecutionContext.Implicits.global
  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load()
    val host = config.getString("database.host")
    val port = config.getInt("database.port")
    val dbName = config.getString("database.dbname")
    val username = config.getString("database.username")
    val password = config.getString("database.password")

    val url = s"jdbc:postgresql://${host}:${port}/${dbName}?ApplicationName=test"
    val connection = Database.forURL(url, username, password, null, "org.postgresql.Driver")


    val brandRequest = sql"select brand from vehicles".as[(String)]

    connection.run(brandRequest).onComplete {
      case Success(brands) => brands

        println(brands)
        var brandSorted: Seq[String] = brands.sorted
        var counter = 0
        var mostCommonBrand: String = ""

        while (counter < brandSorted.size) {
          val brandDrop: Seq[String] = brandSorted.dropWhile(_ == brandSorted.head)
          if ((brandSorted.size - brandDrop.size) > counter) {
            mostCommonBrand = brandSorted.head
            counter = brandSorted.size - brandDrop.size
            brandSorted = brandDrop
          }
          else {
            brandSorted = brandDrop
          }
        }

        val file = new File("test.json")
        val writer = new FileWriter(file, false)

        writer.append(s"""{\n "brand": "${mostCommonBrand}",\n "count": "${counter.toString}"\n}""")
        writer.close()

        println(mostCommonBrand)
        println(counter)
      case Failure(ex) => {
        println(s"failure: ${ex.getMessage}")
        ex.printStackTrace()
      }
    }
    Thread.sleep(3000)
  }
}
