package com.kuymakov.chat.base.mappers

import kotlinx.serialization.json.*


inline fun <reified T> T.toMap(): Map<String, Any?> {
    return json.encodeToJsonElement(this).jsonObject.toMap()
}

fun JsonObject.toMap(): Map<String, Any?> {
    return entries.associate {
        it.key to it.value.toAny()
    }
}

private fun JsonElement.toAny(): Any? {
    return when (this) {
        is JsonNull -> null
        is JsonPrimitive -> content
        is JsonArray -> map { it.toAny() }
        is JsonObject -> toMap()
    }
}

