package konekcija

import akka.actor.{Actor, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.ask
import konekcija.SchedulersHowTo.RandomNumber
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object SchedulersHowTo {
  case class RandomNumber(int: Int)
}

class SchedulersHowTo(i: Double) extends Actor {
  var numbers: Double = i

  override def receive: Receive = {
    case RandomNumber(number) => {
      if (number <= numbers) {
        numbers -= number
        sender ! number
      }
    }
  }
}

object ActorUsage {

  def main(args: Array[String]): Unit = {
    val as = ActorSystem("test-system")
    implicit val timeout = new Timeout(1.seconds)

    val counter = as.actorOf(Props(new SchedulersHowTo(0)))
    as.scheduler.scheduleAtFixedRate(1.second, 2.second) (new Runnable {
      override def run(): Unit = {

        val i = new scala.util.Random
        val r = i.nextInt

        val result: Future[Any] = counter ? RandomNumber(r)
        result.onComplete {
          case Success(r) => println(r)
          case Failure(ex) => println(s"Didn't finish...: ${ex.getMessage}")
        }
      }
    })
  }
}