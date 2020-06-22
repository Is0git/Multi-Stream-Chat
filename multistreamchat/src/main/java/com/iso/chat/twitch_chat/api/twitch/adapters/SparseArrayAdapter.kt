package com.iso.chat.twitch_chat.api.twitch.adapters

import android.util.SparseArray
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class SparseArrayJsonAdapter(
    private val elementAdapter: JsonAdapter<Any?>
) : JsonAdapter<SparseArray<Any?>>() {
    object Factory : JsonAdapter.Factory {
        override fun create(type: Type, annotations: Set<Annotation>,
                            moshi: Moshi
        ): JsonAdapter<*>? {
            if (annotations.isNotEmpty()) return null
            val rawType = Types.getRawType(type)
            if (rawType != SparseArray::class.java) return null
            val elementType = (type as ParameterizedType).actualTypeArguments[0]
            return SparseArrayJsonAdapter(moshi.adapter(elementType))
        }
    }

    override fun fromJson(reader: com.squareup.moshi.JsonReader): SparseArray<Any?>? {
        throw UnsupportedOperationException()
    }

    override fun toJson(writer: com.squareup.moshi.JsonWriter, value: SparseArray<Any?>?) {
        checkNotNull(value, { "Adapter doesn't support null. Wrap with nullSafe()." }).apply {
            writer.beginArray()
            var index = 0
            val size = size()
            while (index < size) {
                elementAdapter.toJson(writer, valueAt(index++))
            }
            writer.endArray()
        }
    }
}

data class WaterWarningItem(val id: Long, val title: String, val warning_lvl: String,
                            val own_limit: String)

fun main(args: Array<String>) {
    val moshi = Moshi.Builder().add(SparseArrayJsonAdapter.Factory).build()
    val adapter = moshi.adapter<SparseArray<WaterWarningItem>>(
        Types.newParameterizedType(SparseArray::class.java, WaterWarningItem::class.java))
    val result = adapter.toJson(SparseArray<WaterWarningItem>(1).apply {
        put(7, WaterWarningItem(5L, "Hello", "Fine", "Okay"))
    })
}