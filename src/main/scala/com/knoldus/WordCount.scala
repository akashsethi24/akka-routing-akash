package com.knoldus

import java.io.File
import akka.actor.{Actor, ActorSystem, Props}
import akka.routing.BalancingPool
import akka.util.Timeout
import scala.concurrent.duration._

/**
  * Created by akash on 19/3/16.
  * This Program will count the word from a  single file
  * This File Contains 3 Actors
  * 1. ProcessLine - this actor forwards each line as a message to other actor
  * 2. CountWord - this actor is using the BalancedPool Router with 2 instances to count the word in each line
  * 3. ShowResult - this actor is receiving individual line result from CountWord actor
  * and showing the integrated result
  */
class ProcessLine extends Actor{

  override def receive: Receive = {

    case file:java.io.File => WordCount.startTime = System.currentTimeMillis()
      for(line <- scala.io.Source.fromFile(file).getLines()) {
      WordCount.countWordRoute ! line
    }

    case _ => println("I was Expecting File not That")
  }
}

class CountWord extends Actor {

  override def receive: Actor.Receive = {
    case line:String => WordCount.showResult ! line.split(" ").length
    case _ => println("I am Done Counting")
  }

}

class ShowResult extends Actor {

  override def receive: Actor.Receive = {
    case count:Int =>
      WordCount.countedLines+=1
      WordCount.count+=count
      if(WordCount.lineCount == WordCount.countedLines) {
        WordCount.endTime = System.currentTimeMillis()
        println("Total Words in a File are :- "+WordCount.count)
        println("Total time Taken is - "+(WordCount.endTime - WordCount.startTime) + " MilliSeconds")
      }

    case _ => println("Something Went Wrong")
  }

}

object WordCount {

    //  The full Path of File
  val file = new File("/home/akash/1.txt")
  var count = 0
  var startTime = 0L
  var endTime = 0L
  var lineCount:Double = scala.io.Source.fromFile(file).getLines().length
  var countedLines = 0
  val system = ActorSystem("Count")
  val processLine = system.actorOf(Props[ProcessLine],"processLine")
  val countWordRoute = system.actorOf(Props[CountWord].withRouter(BalancingPool(2)),"countWord")
  val showResult = system.actorOf(Props[ShowResult],"showResult")
  implicit val timeout = Timeout(5.second)

  def main(args: Array[String]) {

      processLine ! file
    system.terminate()
  }
}