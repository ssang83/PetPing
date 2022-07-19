package ai.comake.petping

import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes

object AirbridgeManager {

    fun trackEvent(category: String, action: String, label: String) {
        val eventValue = 10f
        val eventAttributes = mutableMapOf<String, String>()
        val semanticAttributes = SemanticAttributes()

        Airbridge.trackEvent(
            category,
            action,
            label,
            eventValue,
            eventAttributes,
            semanticAttributes.toMap()
        )
    }
}