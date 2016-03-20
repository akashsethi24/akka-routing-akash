package com.knoldus

import akka.actor.{Actor, ActorRef}

/**
  * Created by akash on 20/3/16.
  */
class WordCountAggregateActor(mapReducer: ActorRef) extends Actor {

  import scala.collection.mutable
  val tempMap = mutable.HashMap[String,Int]().withDefault(word => 0)

  override def receive: Receive = {

    case wordCountMap:Map[String,Int] =>  add(wordCountMap)

    case DONE =>  mapReducer ! (mutable.HashMap[String,Int]() ++ tempMap)
  }

  def add(wordCount:Map[String,Int]) = {
    wordCount.foreach{
      case (word,count) => tempMap(word) += count
    }
  }
}

case object DONE
