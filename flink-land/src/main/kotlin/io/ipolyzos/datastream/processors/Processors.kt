package io.ipolyzos.datastream.processors

import io.ipolyzos.models.clickstream.*
import org.apache.flink.api.common.state.ListState
import org.apache.flink.api.common.state.ListStateDescriptor
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Processors {
    class UserEnrichmentFn : CoProcessFunction<ClickEvent, User, EventWithUser>() {
        private val logger: Logger = LoggerFactory.getLogger(EnrichmentFn::class.java.canonicalName)

        private lateinit var userState: ValueState<User>
        private lateinit var eventBuffer: ListState<ClickEvent>

        override fun open(parameters: Configuration?) {
            logger.info("{}, Initializing state ...", this.javaClass.canonicalName)

            userState = runtimeContext.getState(
                ValueStateDescriptor<User>("users", User::class.java)
            )

            eventBuffer = runtimeContext.getListState(
                ListStateDescriptor<ClickEvent>("events", ClickEvent::class.java)
            )
        }

        override fun processElement1(event: ClickEvent?, ctx: Context?, out: Collector<EventWithUser>?) {
            val user = userState.value()
            user?.let {
                out!!.collect(EventWithUser(event!!, user))
            } ?: kotlin.run {
                logger.warn("Failed to find information for user '{}' - buffering event.", event?.userid)
                eventBuffer.add(event)
            }
        }

        override fun processElement2(user: User?, ctx: Context?, out: Collector<EventWithUser>?) {
            userState.update(user)

            // check if there is any event records waiting for the user info to arrive
            val events: Iterable<ClickEvent> = eventBuffer.get()
            events.forEach {
                logger.info("Found a buffering event - sending it downstream.")
                out!!.collect(EventWithUser(it, user!!))
                eventBuffer.clear()
            }
        }
    }

    class EnrichmentFn : CoProcessFunction<EventWithUser, Product, EnrichedEvent>() {
        private val logger: Logger = LoggerFactory.getLogger(EnrichmentFn::class.java.canonicalName)

        private lateinit var productState: ValueState<Product>
        private lateinit var eventBuffer: ListState<EventWithUser>

        override fun open(parameters: Configuration?) {
            logger.info("{}, Initializing state ...", this.javaClass.canonicalName)

            productState = runtimeContext.getState(
                ValueStateDescriptor<Product>("products", Product::class.java)
            )

            eventBuffer = runtimeContext.getListState(
                ListStateDescriptor<EventWithUser>("events", EventWithUser::class.java)
            )
        }

        override fun processElement1(event: EventWithUser?, ctx: Context?, out: Collector<EnrichedEvent>?) {
            val product = productState.value()
            product?.let {
                out!!.collect(EnrichedEvent(event!!.event, event.user, product))
            } ?: kotlin.run {
                logger.warn("Failed to find information for product '{}' - buffering event.", event!!.event.productId)
                eventBuffer.add(event)
            }
        }

        override fun processElement2(product: Product?, ctx: Context?, out: Collector<EnrichedEvent>?) {
            productState.update(product)

            // check if there is any ride record waiting for the zone info to arrive
            val events: Iterable<EventWithUser> = eventBuffer.get()
            events.forEach {
                logger.info("Found a buffering event - sending it downstream.")
                out!!.collect(EnrichedEvent(it.event, it.user, product!!))
                eventBuffer.clear()
            }
        }
    }
}