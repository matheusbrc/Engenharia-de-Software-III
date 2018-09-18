import akka.actor._
sealed trait Message
case class Start() extends Message
case class Ping() extends Message
case class Pong() extends Message
case class Show(count: Int) extends Message

class AtorA(actB: ActorRef, var start: Int, listener: ActorRef) extends Actor{
    override def receive: Receive = {
        case Start => actB ! Ping
        case Pong => {
            start += 1
            if(start == 2000){
                listener ! Show(start)
                context.stop(self)
            }
            else
                sender ! Ping
        }
    }
    
}

class AtorB extends Actor{
    override def receive: Receive = {
        case Ping => sender ! Pong
    }
    
}

class Listener extends Actor{
    def receive: Receive = {
      case Show(count) =>
        println(count)
        context.system.shutdown()
    }
}

object Main{
    def main(args: Array[String]): Unit = {
        val system = ActorSystem("MainSystem")
        val start: Int = 0
        val listener = system.actorOf(Props[Listener], "listener")
        val actB = system.actorOf(Props[AtorB],"actB")
        val actA = system.actorOf(Props(new AtorA(actB, start, listener)),"actA")
        actA ! Start
    }
}