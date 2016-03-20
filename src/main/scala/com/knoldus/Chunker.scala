package com.knoldus

import java.io.RandomAccessFile

import akka.actor.{Actor, ActorRef}
import akka.routing.Broadcast

/**
  * Created by akash on 20/3/16.
  */
class Chunker(filePath:String,poolReader:ActorRef,blockSize:Long) extends Actor{

  val randomFile = new RandomAccessFile(filePath,"r")
  val fileSize = randomFile.length()
  val noOfChunk = Math.ceil(fileSize / blockSize.toDouble).toInt

  override def receive: Receive = {

    case START => 0 to (noOfChunk-1)  foreach{ chunkIndex =>
      val seek = chunkIndex * blockSize
      val correctedSeek = if(seek == 0) seek else correctIndex(randomFile, seek , -1)
      val correctedTailIndex  = if((seek + blockSize) == fileSize) correctIndex(randomFile, seek+blockSize , +1)
                                else  correctIndex(randomFile,seek+blockSize,-1)
      val correctBufferSize  = (correctedTailIndex-correctedSeek).toInt
      poolReader ! (correctedSeek,correctBufferSize)
    }
      poolReader ! Broadcast(DONE)
  }

  def correctIndex(file:RandomAccessFile,start:Long,move:Int):Long = {
    randomFile.seek(start)
    val read = randomFile.read()
    if (read == ' ' || read == '\n' || read == '\t' || read == -1) start
    else correctIndex(randomFile, start +move, move)
  }
}

case object START
