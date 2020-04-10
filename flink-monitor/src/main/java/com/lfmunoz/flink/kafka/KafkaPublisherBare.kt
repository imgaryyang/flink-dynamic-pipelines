package com.lfmunoz.flink.kafka

import com.lfmunoz.flink.GenericResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.fissore.slf4j.FluentLoggerFactory
import java.util.*


/**
 *
 */
class KafkaPublisherBare {
  companion object {
    private val log = FluentLoggerFactory.getLogger(KafkaPublisherBare::class.java)

    suspend fun connect(aKafkaConfig: KafkaConfig, aFlow: Flow<KafkaMessage>): GenericResult<String> {
      // The producer is thread safe and sharing a single producer instance across threads will generally be faster than
      // having multiple instances.
      log.info().log("[kafka producer connecting] - {}", aKafkaConfig)
      val aKafkaProducer = KafkaProducer<ByteArray, ByteArray>(producerProps(aKafkaConfig))
      return try {
        aFlow.collect { item ->
          aKafkaProducer.send(ProducerRecord(aKafkaConfig.producerTopic, item.key, item.value)) { metadata: RecordMetadata, e: Exception? ->
            e?.let {
              e.printStackTrace()
            } ?: log.trace().log("The offset of the record we just sent is: " + metadata.offset())
          }
        }
        GenericResult.Success("OK")
      } catch (e: Exception) {
        log.error().withCause(e).log("[kafka producer error] - captured exception")
        GenericResult.Failure("NOK", e)
      } finally {
        aKafkaProducer.close()
      }
    }

    private fun producerProps(aKafkaConfig: KafkaConfig): Properties {
      val serializer = ByteArraySerializer::class.java.canonicalName
      val props = Properties()
      props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, aKafkaConfig.bootstrapServer)
      props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer)
      props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer)
      return props
    }

  }

}// end of KafkaPublisherBare
