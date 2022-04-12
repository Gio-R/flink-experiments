package org.myorg.quickstart

import java.util.concurrent.TimeUnit

import collection.JavaConverters._
import org.apache.flink.api.java.utils.ParameterTool
// import org.apache.flink.api.java.operators._
import org.apache.flink.api.common.eventtime._
import org.apache.flink.api.scala._
import org.apache.flink.api.connector.sink.Sink
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.source._
import org.apache.flink.connector.kafka.source.enumerator.initializer._
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema
import org.apache.flink.connector.kafka.sink.KafkaSink
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.flink.streaming.api.environment._
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.kafka.common.serialization._
import org.slf4j.LoggerFactory

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.core.fs.Path
import org.apache.flink.connector.file.sink.FileSink
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy

import org.opensky.api.OpenSkyApi
// import org.opensky.model.OpenSkyStates

object KafkaApp {
  def main(args: Array[String]): Unit = {
    val params = ParameterTool.fromArgs(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getConfig.setGlobalJobParameters(params)

    val inputTopic = "input-topic"
    val outputTopic = "output-topic"
    val brokers = "kafka:9092"
    val group = "my-group"
    val source: KafkaSource[String] = KafkaSource.builder()
                            .setBootstrapServers(brokers)
                            .setTopics(inputTopic)
                            // .setGroupId(group)
                            .setStartingOffsets(OffsetsInitializer.earliest())
                            .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(classOf[StringDeserializer]))
                            .build()

    val kafka = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

    val kafkaSink: KafkaSink[String] = KafkaSink.builder()
                        .setBootstrapServers(brokers)
                        .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                                                                           .setTopic(outputTopic)
                                                                           .setKafkaValueSerializer(classOf[StringSerializer])
                                                                           .build()
                        )
                        .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                        .build()

    kafka.map { _.toUpperCase }     
         .sinkTo(kafkaSink)

    env.execute("KafkaApp") // needed to avoid the No Job Found error
  }

}
