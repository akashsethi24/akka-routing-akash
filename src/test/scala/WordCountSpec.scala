import java.io.File

import akka.actor.ActorSystem
import akka.testkit.{TestActors, ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import scala.concurrent.duration._
/**
  * Created by akash on 19/3/16.
  */
class WordCountSpec extends TestKit(ActorSystem("Count"))
  with FunSuiteLike with BeforeAndAfterAll with ImplicitSender{

  val countRef = system.actorOf(TestActors.echoActorProps)
  implicit val timeout = Timeout(5.seconds)
  val file = new File("/home/akash/1.txt")

  override def afterAll: Unit ={
    TestKit.shutdownActorSystem(system)
  }

  test("Should Receive a file as a Message "){
    countRef ! file
    expectMsg(file)
  }
  test("Should Receive a String as a Message "){
    countRef ! "Line"
    expectMsg("Line")
  }
  test("Should Receive a Int as a Message "){
    countRef ! 24
    expectMsg(24)
  }
  test("Should Receive a Random value as a Message used in _"){
    countRef ! 1L
    expectMsg(1L)
  }
}
