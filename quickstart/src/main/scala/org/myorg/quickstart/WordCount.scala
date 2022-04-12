package org.myorg.quickstart

import org.apache.flink.api.common.functions.FlatMapFunction
import org.apache.flink.api.scala._

object WordCount {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment
    // get input data
    val text: DataSet[String] = env.fromElements("To be, or not to be,--that is the question:--",
                                                 "Whether 'tis nobler in the mind to suffer", 
                                                 "The slings and arrows of outrageous fortune",
                                                 "Or to take arms against a sea of troubles,")

    // val flatMapFunction: FlatMapFunction[String, String] = s => s.split("\\W+")

    val counts = text.flatMap { _.toLowerCase.split("\\W+") }
                     .map { (_, 1) }
                     .groupBy(0)
                     .sum(1)

    // emit result and print result
    // val sink = StreamingFileSink.forRowFormat(new Path("/opt/flink/usrlib/out.txt"), new SimpleStringEncoder<String>())
    counts.printOnTaskManager("WordCount")
    counts.writeAsCsv("/usr/local/flink/output.txt", "\n", " ")
    // return counts

    env.execute("WordCount") // needed to avoid the No Job Found error
  }

}
