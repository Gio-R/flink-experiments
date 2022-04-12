package org.myorg.quickstart

import collection.JavaConverters._
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.opensky.api.OpenSkyApi
// import org.opensky.model.OpenSkyStates

object OpenSky {
  def main(args: Array[String]): Unit = {
    val params = ParameterTool.fromArgs(args)
    val env = ExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(params)

    // val username = if (params.has("username")) { params.get("username") } else { "user" }
    // val password = if (params.has("password")) { params.get("password") } else { "pswd" }

    val api = new OpenSkyApi()
    // val os = api.getStates(0, null, new OpenSkyApi.BoundingBox(45.8389, 47.8229, 5.9962, 10.5226)) // Switzerland
    val os = api.getStates(0, null, new OpenSkyApi.BoundingBox(36.54, 47.12, 6.56, 18.62)) // Italy
    // val os = api.getStates(0, null)
    val svs = env.fromCollection(os.getStates().asScala)

    val vMean = svs.map(sv => (sv.getIcao24(), sv.getVelocity()))

    vMean.writeAsCsv("/usr/local/flink/output.txt", "\n", " ")

    env.execute("OpenSky") // needed to avoid the No Job Found error
  }

}
