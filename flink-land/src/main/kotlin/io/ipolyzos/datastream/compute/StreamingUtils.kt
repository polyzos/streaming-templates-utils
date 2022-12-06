package io.ipolyzos.datastream.compute

import io.ipolyzos.config.KafkaConfig
import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import java.util.Properties

object StreamingUtils {
    fun <T> initDatastream(topicName: String,
                           groupId: String,
                           offsets: OffsetsInitializer,
                           schema: DeserializationSchema<T>,
                           properties: Properties): KafkaSource<T> {
        return KafkaSource.builder<T>()
            .setBootstrapServers(KafkaConfig.bootstrapServers())
            .setTopics(topicName)
            .setGroupId(groupId)
            .setStartingOffsets(offsets)
            .setValueOnlyDeserializer(schema)
            .setProperties(properties)
            .build()
    }
}