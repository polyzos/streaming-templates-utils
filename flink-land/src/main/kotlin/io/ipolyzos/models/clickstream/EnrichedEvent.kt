package io.ipolyzos.models.clickstream

data class EnrichedEvent(val event: ClickEvent, val user: User, val product: Product)
