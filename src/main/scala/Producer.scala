import java.util.Properties

import java.time.Instant
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.GenericData
import org.apache.kafka.clients.producer.{ KafkaProducer, ProducerRecord }
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Producer extends App {
  val logger = Logger(LoggerFactory.getLogger("ProducerLogger"))

  logger.info("Waiting for schema registry to fully start")
  Thread.sleep(100000)

  val kafkaBootstrapServer = "kafka:29092"
  val schemaRegistryUrl = "http://schema-registry:8081"
  val topicName = "test"

  val props = new Properties()
  props.put("bootstrap.servers", kafkaBootstrapServer)
  props.put("schema.registry.url", schemaRegistryUrl)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("acks", "1")

  val producer = new KafkaProducer[String, GenericData.Record](props)
  val schemaParser = new Parser

  val valueSchemaJson = s"""{
                           |  "type":"record",
                           |  "name":"LogSample",
                           |  "fields":[
                           |     {
                           |        "name":"uuid",
                           |        "type":[
                           |           "null",
                           |           "string"
                           |        ],
                           |        "default":null
                           |     },
                           |     {
                           |        "name":"timestamp",
                           |        "type":[
                           |          "null",
                           |          "long"
                           |         ],
                           |         "default":null
                           |     }
                           |  ]
                           |}""".stripMargin

  val valueSchemaAvro = schemaParser.parse(valueSchemaJson)
  val avroRecord = new GenericData.Record(valueSchemaAvro)

  try {
    while (true) {

      avroRecord.put("uuid", java.util.UUID.randomUUID.toString)
      avroRecord.put("timestamp", Instant.now.toEpochMilli)

      val record = new ProducerRecord(topicName, topicName + "_Key", avroRecord)
      val ack = producer.send(record).get()

      logger.info(s"${ack.toString} written to partition ${ack.partition.toString}")
      logger.info(s"sleep for one second...")
      Thread.sleep(1000)
    }
  }
  catch {
    case e: Throwable => logger.error(e.getMessage, e)
  }
  finally {
    producer.close()
  }
}