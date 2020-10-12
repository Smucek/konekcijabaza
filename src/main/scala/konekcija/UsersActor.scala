package konekcija

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.duration._

object UsersActor {

    def main(args: Array[String]): Unit = {


      val as = ActorSystem("users")
      implicit val timeout = new Timeout(2.seconds)

//      val operations = as.actorOf(Props(new UsersActorOperations))

//      operations ! ListUsers
    }
}
