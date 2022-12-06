package io.ipolyzos.datastream.compute

import io.ipolyzos.config.KafkaConfig
import io.ipolyzos.models.clickstream.*
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.configuration.Configuration
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.datastream.DataStream
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import java.time.Duration
import io.ipolyzos.datastream.serdes.Serdes
import io.ipolyzos.datastream.processors.Processors
import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.time.Time
import org.apache.flink.contrib.streaming.state.EmbeddedRocksDBStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig

class EnrichmentStream {
    private val CHECKPOINTS_DIR     = "file://${System.getProperty("user.dir")}/checkpoints/"
    private val ROCKSDB_STATE_DIR   = "file://${System.getProperty("user.dir")}/state/rocksdb"

    companion object {
        @JvmStatic fun main(args : Array<String>) {
            EnrichmentStream().runStream()
        }
    }

    fun runStream() {
        val environment = StreamExecutionEnvironment
            .createLocalEnvironmentWithWebUI(Configuration())

        environment.parallelism = 1

        // Checkpoint Configurations
        environment.enableCheckpointing(5000)
        environment.checkpointConfig.minPauseBetweenCheckpoints = 100
        environment.checkpointConfig.setCheckpointStorage(CHECKPOINTS_DIR)

        val stateBackend = EmbeddedRocksDBStateBackend()
        stateBackend.setDbStoragePath(ROCKSDB_STATE_DIR)
//        stateBackend.numberOfTransferThreads = 2
        environment.stateBackend = stateBackend

        environment.checkpointConfig.externalizedCheckpointCleanup =
            CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION

        // Configure Restart Strategy
        environment.restartStrategy = RestartStrategies.fixedDelayRestart(5, Time.seconds(5))

        val properties = KafkaConfig.buildConsumerProps()

        val eventSource: KafkaSource<ClickEvent> = StreamingUtils.initDatastream<ClickEvent>(
            KafkaConfig.EVENTS_TOPIC,
            "ecommerce.events.flink.group",
            OffsetsInitializer.earliest(),
            Serdes.EventDeSchema(),
            properties
        )

        val userSource: KafkaSource<User> = StreamingUtils.initDatastream<User>(
            KafkaConfig.USERS_TOPIC,
            "ecommerce.users.flink.group",
            OffsetsInitializer.earliest(),
            Serdes.UserDeSchema(),
            properties
        )

        val productSource: KafkaSource<Product> = StreamingUtils.initDatastream<Product>(
            KafkaConfig.PRODUCTS_TOPIC,
            "ecommerce.products.flink.group",
            OffsetsInitializer.earliest(),
            Serdes.ProductDeSchema(),
            properties
        )

        val watermarkStrategy: WatermarkStrategy<ClickEvent> =
            WatermarkStrategy
                .forBoundedOutOfOrderness<ClickEvent>(Duration.ofSeconds(5))
                .withTimestampAssigner(
                    SerializableTimestampAssigner { event: ClickEvent, _: Long ->
                        event.eventTime
                    } as SerializableTimestampAssigner<ClickEvent>?
                )

        val eventStream: DataStream<ClickEvent> = environment
            .fromSource(eventSource, watermarkStrategy, "Event Source")
            .name("EventSource")
            .uid("EventSource")

        val userStream: DataStream<User> = environment
            .fromSource(userSource, WatermarkStrategy.noWatermarks(), "User Source")
            .name("UserSource")
            .uid("UserSource")

        val productStream: DataStream<Product> = environment
            .fromSource(productSource, WatermarkStrategy.noWatermarks(), "Product Source")
            .name("ProductSource")
            .uid("ProductSource")

        val userEnrichmentStream: DataStream<EventWithUser> = eventStream
            .keyBy(ClickEvent::userid)
            .connect(userStream.keyBy(User::userId))
            .process(Processors.UserEnrichmentFn())
            .name("UserEnrichmentFn")
            .uid("UserEnrichmentFn")

        val enrichedStream: DataStream<EnrichedEvent> = userEnrichmentStream
            .keyBy { e-> e.event.productId }
            .connect(productStream.keyBy(Product::productCode))
            .process(Processors.EnrichmentFn())
            .name("EnrichmentFn")
            .uid("EnrichmentFn")

        enrichedStream.print()
        environment.execute("Event Enrichment Stream")
    }
}