package com.knoldus

import java.io.RandomAccessFile
import akka.actor.{Actor, ActorRef, Props}

/**
  * Created by akash on 20/3/16.
  */
class PoolReader(filePath:String,mapReducer: ActorRef) extends Actor{

  val wordCountActor = context.actorOf(Props(new WordCountActor(mapReducer)))
  val randomFile = new RandomAccessFile(filePath,"r")
  val channel = randomFile.getChannel

  override def receive: Receive = {

    case(chunkIndex:Long,bufferSize:Int) => wordCountActor ! readChunk(chunkIndex,bufferSize)

    case DONE => wordCountActor ! DONE
  }
  def readChunk(index:Long,buffer:Int):String = {

    val byteBuffer = new Array[Byte](buffer)
    randomFile.seek(index)
    randomFile.read(byteBuffer)
    new  String(byteBuffer)

  }

}
