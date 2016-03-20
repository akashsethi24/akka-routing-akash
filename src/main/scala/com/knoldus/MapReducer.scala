package com.knoldus

import akka.actor.Actor
/**
  * Created by akash on 20/3/16.
  */
class MapReducer(poolSize:Int) extends Actor{

  import scala.collection.mutable
  val wordMap = mutable.HashMap[String,Int]().withDefault(word => 0)
  var countTillPool:Int = 0

  override def receive: Receive = {

    case resultMap:mutable.HashMap[String,Int] =>
      countTillPool+=1
      resultMap.foreach {
      case  (word, count) => wordMap(word) += count
      }
      if(countTillPool == poolSize) {
        println("Output is \n"+wordMap)
      }
  }
}
