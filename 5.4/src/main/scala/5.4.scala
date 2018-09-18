import akka.actor._
import akka.routing.RoundRobinRouter
import scala.concurrent.duration._
import scala.io._
sealed trait Message
case object Calculate extends Message
case class Work(f: Int) extends Message
case class Result(valor: Double) extends Message
case class Show(conta: Double, duration: Duration) extends Message

class Worker extends Actor{
    
    def conta(f: Int): Double = {
        var fatorial = 1
        if(f != 0 && f != 1)
        {
            for(i <- 2 until f+1)
                fatorial *= i
        }
        return fatorial
    }
    
    override def receive: Receive = {
        case Work(f) => sender ! Result(conta(f))
    }
}
class Master(workers: Int, msgs: Int, listener: ActorRef) extends Actor{
    var conta: Double = _
    var resultados: Int = _
    val start: Long = System.currentTimeMillis
 
    val worker = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(workers)), "worker")
      
    override def receive: Receive = {
        case Calculate => {
                worker ! Work(msgs)
        }
        
        case Result(res) => {
            conta += res
            listener ! Show(conta, (System.currentTimeMillis - start).millis)
            context.stop(self)
        }
    }
}

class Listener extends Actor{
    def receive: Receive = {
      case Show(res, duration) ⇒
        println("Resultado: \t\t%s\n\tTempo: \t%s".format(res, duration))
        context.system.shutdown()
    }
}

object Teste{
    def main(args: Array[String]): Unit = {
        val system = ActorSystem("MainSystem")
        println("Digite os trabalhadores ")
        val workers = StdIn.readInt()
        println("Digite o fatorial ")
        val f = StdIn.readInt()
        val listener = system.actorOf(Props[Listener], "listener")
        val masterActor = system.actorOf(Props(new Master(workers, f, listener)),"masterActor")
        
        masterActor ! Calculate
    }
}




