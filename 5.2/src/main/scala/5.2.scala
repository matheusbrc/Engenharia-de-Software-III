import akka.actor._
import akka.routing.RoundRobinRouter
import scala.io._
import scala.util.Random
sealed trait Message
case object Sorteio extends Message
case class Sortear(start: Int) extends Message

class Worker extends Actor{
    val r = scala.util.Random
    var n: Int = _
    
    override def receive: Receive = {
        case Sortear(start: Int) => {
            var count = 0
            println(s"Valor: $start")
            for(i <- 1 until 33){
                n = r.nextInt(200)
                println(s"$i- $n")
                if(n == start)
                    count += 1
            }
            println(s"Acertos: $count")
        }
    }
}
class Master(workers: Int) extends Actor{
    val r = scala.util.Random
    var n: Int = r.nextInt(200)
 
    val worker = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(workers)), "worker")
      
    override def receive: Receive = {
        case Sorteio => {
            worker ! Sortear(n)
        }
    }
}

object Teste{
    def main(args: Array[String]): Unit = {
        val system = ActorSystem("MainSystem")
        val masterActor = system.actorOf(Props(new Master(32)),"masterAct")
        
        masterActor ! Sorteio
    }
}




