package konekcija

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives
import akka.pattern.ask
import akka.util.Timeout
import konekcija.DatabaseActor._
import pdi.jwt.{JwtAlgorithm, JwtClaim, JwtSprayJson}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

object UsersApi extends Directives with JsonSuppUser {

  implicit val as = ActorSystem()
  val dbActor = as.actorOf(Props(new DatabaseActor))
  implicit val timeout = Timeout(3.seconds)


  val algorithm = JwtAlgorithm.HS256
  val secretKey = "imamjosmnogouciti"

  def createToken(username: String, expiration: Int): String = {
    val claims = JwtClaim(
      expiration = Some(System.currentTimeMillis()/1000 + TimeUnit.DAYS.toSeconds(expiration)),
      issuedAt = Some(System.currentTimeMillis()/1000),
      issuer = Some("ajdin"),
      content = "scalarules"
    )
    JwtSprayJson.encode(claims, secretKey, algorithm)
  }

  val login = get {
    path("getUser") {
      parameters("username".as[String], "pass".as[String]) { (username, pass) =>

        val passDecoded = new String(java.util.Base64.getDecoder.decode(pass))
        val passSplited: Seq[String] = passDecoded.split(":")
        val passFinal = passSplited(1)

        val userConfirmed = (dbActor ? GetUser(username, passFinal))
        onComplete(userConfirmed) {
          case Success(user) => {
            val token = createToken(username, 1)
            respondWithHeader(RawHeader("Access-Granted", token)) {
              complete(StatusCodes.OK)
            }
          }
          case Failure(ex) => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }

//  val routes = login

//  val httpCtx = Http()
//  httpCtx.bindAndHandle(routes,"localhost", 8090)

}
