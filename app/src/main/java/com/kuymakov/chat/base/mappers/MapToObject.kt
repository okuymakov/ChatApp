package com.kuymakov.chat.base.mappers

import com.google.firebase.Timestamp
import kotlinx.serialization.json.*

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

inline fun <reified T> Map<String, Any?>.toObject(): T {
    val map = entries.associate {
        it.key to it.value.toJsonElement()
    }
    return json.decodeFromJsonElement(JsonObject(map))
}

fun Any?.toJsonElement(): JsonElement =
    when (this) {
        null -> JsonNull
        is Map<*, *> -> toJsonElement()
        is Collection<*> -> toJsonElement()
        is Boolean -> JsonPrimitive(this)
        is Number -> JsonPrimitive(this)
        is String -> JsonPrimitive(this)
        is Enum<*> -> JsonPrimitive(this.toString())
        is Timestamp -> JsonPrimitive(this.toDate().time)
        else -> throw IllegalStateException("Can't serialize unknown type: $this")
    }

private fun Collection<*>.toJsonElement(): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    this.forEach { value ->
        when (value) {
            null -> list.add(JsonNull)
            is Map<*, *> -> list.add(value.toJsonElement())
            is Collection<*> -> list.add(value.toJsonElement())
            is Boolean -> list.add(JsonPrimitive(value))
            is Number -> list.add(JsonPrimitive(value))
            is String -> list.add(JsonPrimitive(value))
            is Enum<*> -> list.add(JsonPrimitive(value.toString()))
            is Timestamp -> list.add(json.encodeToJsonElement(value.toDate()))
            else -> throw IllegalStateException("Can't serialize unknown collection type: $value")
        }
    }
    return JsonArray(list)
}

private fun Map<*, *>.toJsonElement(): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    this.forEach { (key, value) ->
        key as String
        when (value) {
            null -> map[key] = JsonNull
            is Map<*, *> -> map[key] = value.toJsonElement()
            is Collection<*> -> map[key] = value.toJsonElement()
            is Boolean -> map[key] = JsonPrimitive(value)
            is Number -> map[key] = JsonPrimitive(value)
            is String -> map[key] = JsonPrimitive(value)
            is Enum<*> -> map[key] = JsonPrimitive(value.toString())
            is Timestamp -> map[key] = json.encodeToJsonElement(value.toDate())
            else -> throw IllegalStateException("Can't serialize unknown type: $value")
        }
    }
    return JsonObject(map)
}