package com.knoldus

import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by akash on 20/3/16.
  */
class WordCountActor(mapReducer: ActorRef) extends Actor {
  val countCombiner = context.actorOf(Props(new WordCountAggregateActor(mapReducer)))

  override def receive: Receive = {

    case chunk: String =>
      val wordCount = getWordCount(chunk)
      countCombiner ! wordCount

    case DONE => countCombiner ! DONE

  }
  def getWordCount(chunk:String):Map[String,Int] ={
    chunk.trim.split("\\s+").groupBy(word =>word).mapValues(word => word.length)
  }

}

case object GIVEME
