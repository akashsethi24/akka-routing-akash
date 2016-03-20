package com.knoldus

import akka.actor.{ActorSystem, Props}
import akka.routing.SmallestMailboxPool

/**
  * Created by akash on 20/3/16.
  */
object WordCountFromFile {

  val blockSize = 1024 * 1024 *2
  val filePath="/home/akash/1.txt"
  val poolSize = Runtime.getRuntime.availableProcessors() * 2
  val system = ActorSystem("WordCount")
  val mapReducer = system.actorOf(Props(new MapReducer(poolSize)),"mapReducer")
  val poolReader = system.actorOf(SmallestMailboxPool(poolSize).props(Props(new PoolReader(filePath,mapReducer))),name = "Count")
  val chunker =   system.actorOf(Props(new Chunker(filePath,poolReader,blockSize)),"chunker")

  def main(args: Array[String]) {
    chunker ! START
  }

}
