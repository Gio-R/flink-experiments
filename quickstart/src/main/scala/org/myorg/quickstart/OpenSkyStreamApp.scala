package org.myorg.quickstart

import collection.JavaConverters._
import org.apache.flink.api.java.utils.ParameterTool

import org.apache.flink.api.common.eventtime._
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.environment._
import org.apache.flink.streaming.api.functions.sink.SocketClientSink

import org.opensky.api.OpenSkyApi
// import org.opensky.model.OpenSkyStates

object OpenSkyStreamApp {
  def main(args: Array[String]): Unit = {
    val params = ParameterTool.fromArgs(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(params)

    val sourceFunction = new OpenSkySourceFunction(36.54, 47.12, 6.56, 18.62)

    val source = env.addSource(sourceFunction)

    val socketSink = new SocketClientSink[String]("frontend", 8080, new SimpleStringSchema())

    source.map { _.getIcao24() } //.map(sv => (sv.getIcao24(), sv.getVelocity()))
          .map(sv => s"$sv\n")
          .addSink(socketSink)

    env.execute("OpenSkyStreamApp") // needed to avoid the No Job Found error
  }

}
