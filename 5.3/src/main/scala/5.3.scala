import akka.actor._
sealed trait Message
case class Start(s: String) extends Message
case class mensagem(s: String) extends Message

class DataSource(prox: ActorRef, var start: String) extends Actor{
    override def receive: Receive = {
        case Start(s) => {
            println("DataSource " + s)
            prox ! mensagem(s)
        }
    }
    
}

class LowerCase(prox: ActorRef) extends Actor{
    override def receive: Receive = {
        case mensagem(s) => {
            var lc: String = s.toLowerCase()
            println("LowerCase " + lc)
            prox ! mensagem(lc)
        }
    }
    
}

class UpperCase(prox: ActorRef) extends Actor{
    override def receive: Receive = {
        case mensagem(s) => {
            var uc: String = s.toUpperCase()
            println("UpperCase " + uc)
            prox ! mensagem(uc)
        }
    }
    
}

class FilterVowels(prox: ActorRef) extends Actor{
    override def receive: Receive = {
        case mensagem(s) => {
            var fv: String = ""
            for(i <- 0 until s.length()){
                if(s.charAt(i) != 'a' && s.charAt(i) != 'A'
                && s.charAt(i) != 'e' && s.charAt(i) != 'E'
                && s.charAt(i) != 'i' && s.charAt(i) != 'I'
                && s.charAt(i) != 'o' && s.charAt(i) != 'O'
                && s.charAt(i) != 'u' && s.charAt(i) != 'U'){
                    fv += s.charAt(i)
                }
            }
            println("FilterVowels " + fv)
            prox ! mensagem(fv)
        }
    }
    
}

class Duplicate extends Actor{
    override def receive: Receive = {
        case mensagem(s) => {
            var d: String = s+s
            println("Duplicate " + d)
        }
    }
    
}

object Main{
    def main(args: Array[String]): Unit = {
        val system = ActorSystem("MainSystem")
        val start: String = "String"
        val Duplicate = system.actorOf(Props[Duplicate],"Duplicate")
        val FilterVowels = system.actorOf(Props(new FilterVowels(Duplicate)),"FilterVowels")
        val UpperCase = system.actorOf(Props(new UpperCase(FilterVowels)),"UpperCase")
        val LowerCase = system.actorOf(Props(new LowerCase(UpperCase)),"LowerCase")
        val DataSource = system.actorOf(Props(new DataSource(LowerCase, start)),"DataSource")
        DataSource ! Start(start)
    }
}