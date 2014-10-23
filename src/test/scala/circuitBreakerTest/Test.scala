package circuitBreakerTest

import akka.pattern.CircuitBreaker
import concurrent.duration._
import akka.actor.ActorSystem
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration.Infinite

object Test extends App {

  override def main(args: Array[String]) {
    val system = ActorSystem("test")
    val circuitBreaker = CircuitBreaker(system.scheduler,
      maxFailures = 1,
      callTimeout = 1 second,
      resetTimeout = 60 seconds)

    (1 to 3).foreach { i =>
      val result = circuitBreaker.withCircuitBreaker(
        Future {
          println(s"Started $i")
          Thread.sleep(3000)
          println(s"Finished $i")
          "Success"
        })

      val recovered = result.recover { case ex => s"Failed with ${ex.getMessage}" }
      val value = Await.result(recovered, Duration.Inf)

      println(s"Result for $i: " + value)
    }
    system.shutdown();

  }

}