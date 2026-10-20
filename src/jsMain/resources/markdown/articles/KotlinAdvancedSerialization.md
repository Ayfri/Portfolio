---
nav-title: Mastering kotlinx.serialization
title: "Mastering kotlinx.serialization: Advanced Techniques, Custom Serializers and Performance"
description: Advanced kotlinx.serialization techniques from a real Kotlin Multiplatform library, custom KSerializer, enum strategies, @KeepGeneratedSerializer, reflection-free polymorphic serialization with KSP, JSON and NBT, and measured performance optimizations.
keywords: kotlinx.serialization, kotlin serialization, custom serializer, KSerializer, enum serialization, polymorphic serialization, sealed class serialization, sealed interface serialization, KeepGeneratedSerializer, generatedSerializer, JsonTransformingSerializer, JsonNamingStrategy, kotlin multiplatform serialization, kotlin/js serialization, ksp code generation, reflection-free serialization, kotlinx serialization performance, kotlin json serialization tutorial, kotlin serialization best practices 2026
date-created: 2024-12-01
date-modified: 2026-10-20
root: .layouts.ArticleLayout
routeOverride: /articles/kotlin-advanced-serialization/index
---

Explore the powerful features of [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) with this comprehensive guide.
Every technique here runs in production in [Kore](https://github.com/Ayfri/Kore), a Kotlin Multiplatform library that writes
Minecraft data packs, and every performance number comes from a measured commit.

---

## 1. Introduction to kotlinx.serialization

**kotlinx.serialization** is a robust, multiplatform serialization library for Kotlin, designed to effortlessly convert Kotlin objects to
and from various data formats such as JSON, ProtoBuf, CBOR, and more. A compiler plugin generates a `KSerializer` for every
`@Serializable` class at build time, so the library itself needs no reflection.

### Why Advanced Techniques?

While the default serialization capabilities are sufficient for many use cases, complex projects often demand custom serialization
strategies to handle specific requirements like polymorphism, custom naming conventions, or performance optimizations. Mastering these
advanced techniques ensures your serialization logic remains maintainable, efficient, and tailored to your application's needs.

### The Setup Behind This Guide

[Kore](https://kore.ayfri.com) serializes hundreds of Minecraft formats (advancements, predicates, worldgen, item components, chat
components) to JSON and to NBT, Minecraft's binary tag format. It uses:

- **Kotlin 2.4** and **kotlinx.serialization 1.11**.
- **Kotlin Multiplatform**, targeting the JVM and JS (Kore also runs in a browser playground). `kotlin-reflect` calls like
  `memberProperties` or `createType()` don't exist on JS, so every serializer here is reflection-free.
- **[knbt](https://github.com/BenWoodworth/knbt)** for the NBT format. The NBT branches in the snippets below are optional, drop them
  if you only target JSON.
- **[KSP](https://kotlinlang.org/docs/ksp-overview.html)** to generate the code reflection would otherwise compute at runtime.

You can explore Kore at [kore.ayfri.com](https://kore.ayfri.com) or on GitHub at
[github.com/Ayfri/Kore](https://github.com/Ayfri/Kore).

## 2. Crafting Custom Serializers

Custom serializers allow you to override the default serialization behavior, providing fine-grained control over how your objects are
serialized and deserialized.

### When to Customize

- **Non-Standard Data Formats**: When dealing with APIs that expect a specific data structure not directly supported by default serializers.
- **Shape Simplification**: When a class should collapse to a bare value in some cases, like `true` instead of `{"enabled":true}`.
- **Polymorphic Serialization**: When the built-in class discriminator doesn't match the format you have to produce.
- **Performance**: When the default path builds intermediate trees or loads classes you don't need.

### Implementing `KSerializer`

A `KSerializer<T>` has three members: a `descriptor` describing the serialized shape, `serialize` and `deserialize`. Here's a base class
for every enum that serializes to a transformed string, with a lookup map built once so decoding is a single hash lookup instead of a
scan over the entries:

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

open class EnumStringSerializer<T : Enum<T>>(
	private val values: EnumEntries<T>,
	private val encode: T.() -> String,
) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor("EnumStringSerializer", PrimitiveKind.STRING)

	private val byEncoded = lazy { values.associateBy { it.encode() } }

	/** The entry serialized as [string], `null` when there is none. */
	protected open fun decode(string: String) = byEncoded.value[string]

	override fun deserialize(decoder: Decoder): T {
		val string = decoder.decodeString()
		return decode(string) ?: throw SerializationException("'$string' is not an entry of this enum.")
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.encode())
}
```

**Explanation:**

- **Descriptor**: A `STRING` primitive, since every entry becomes a plain JSON string.
- **serialize**: Writes the result of the `encode` transform.
- **deserialize**: Looks the string up in a map built on first use, and throws a `SerializationException` with the bad value instead of
  a bare `NoSuchElementException`.

---

## 3. Enum Serialization Strategies

Enums often require specific serialization formats to align with backend expectations or external APIs. For a one-off rename,
`@SerialName("...")` on an entry is enough. When a whole family of enums follows the same rule, a reusable serializer saves you from
annotating every entry.

### UppercaseSerializer and LowercaseSerializer

Both are one-liners on top of `EnumStringSerializer`:

```kotlin
open class UppercaseSerializer<T : Enum<T>>(values: EnumEntries<T>) : EnumStringSerializer<T>(values, { name.uppercase() })

/** Serializes an entry as [transform], its lowercase name by default, decoding also accepts that name with or without `minecraft:`. */
open class LowercaseSerializer<T : Enum<T>>(
	values: EnumEntries<T>,
	transform: T.() -> String = { name.lowercase() },
) : EnumStringSerializer<T>(values, transform) {
	private val byName = lazy { values.associateBy { it.name.lowercase() } }

	override fun decode(string: String) = super.decode(string) ?: byName.value[string.removePrefix("minecraft:")]
}
```

**Usage Example:**

```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable(with = Status.Companion.StatusSerializer::class)
enum class Status {
	ACTIVE,
	INACTIVE,
	PENDING;

	companion object {
		data object StatusSerializer : UppercaseSerializer<Status>(entries)
	}
}

@Serializable
data class User(val status: Status)

println(Json.encodeToString(User(Status.ACTIVE)))
```

Resulting JSON:

```json
{
	"status": "ACTIVE"
}
```

### Custom Strings Through a Transform

The transform handles the entries whose serialized name isn't derivable from the Kotlin name. Since decoding uses the same transform,
the custom strings round-trip too:

```kotlin
@Serializable(with = StructureRotation.Companion.RotationSerializer::class)
enum class StructureRotation {
	NONE,
	CLOCKWISE_90,
	ROT_180,
	COUNTERCLOCKWISE_90;

	companion object {
		data object RotationSerializer : LowercaseSerializer<StructureRotation>(entries, transform = {
			if (this == ROT_180) "180" else name.lowercase()
		})
	}
}

Json.encodeToString(StructureRotation.ROT_180)                          // "180"
Json.decodeFromString<StructureRotation>("\"180\"")                     // ROT_180
Json.decodeFromString<StructureRotation>("\"minecraft:clockwise_90\"") // CLOCKWISE_90
```

A camelCase serializer is the same one-liner with a `camelCase()` string helper as the transform.

### EnumOrdinalSerializer

Enumerations have a predefined order based on their declaration sequence. The `EnumOrdinalSerializer` serializes enums based on their
ordinal values:

```kotlin
open class EnumOrdinalSerializer<T : Enum<T>>(private val values: EnumEntries<T>, serialName: String) : KSerializer<T> {
	override val descriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder): T {
		val index = decoder.decodeInt()
		return values.getOrNull(index) ?: throw SerializationException("$index is not an ordinal of ${descriptor.serialName}.")
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeInt(value.ordinal)
}

data object StatusOrdinalSerializer : EnumOrdinalSerializer<Status>(Status.entries, "StatusOrdinal")

Json.encodeToString(StatusOrdinalSerializer, Status.INACTIVE) // 1
```

Keep in mind that ordinals change when someone reorders the entries, so only use them for formats you control on both ends.

---

## 4. Wrapping the Generated Serializer with `@KeepGeneratedSerializer`

Most custom serializers only need to tweak the output of the plugin-generated one: inline a property, drop a wrapper object, rename a key.
The naive way rebuilds the object by hand with reflection (`memberProperties`, `getter.call`), which is slow, JVM-only and ignores
`@SerialName` unless you reimplement it.

`@KeepGeneratedSerializer` keeps the plugin serializer around when you set your own with `@Serializable(with = ...)`, and exposes it
through `generatedSerializer()` in the companion. Your serializer then **delegates** to it and post-processes the resulting tree:

```kotlin
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = Config.Companion.ConfigSerializer::class)
data class Config(val enabled: Boolean, val retryCount: Int? = null) {
	companion object {
		data object ConfigSerializer : SinglePropertySimplifierSerializer<Config>(generatedSerializer(), "enabled")
	}
}
```

Things to know:

- It's an `@ExperimentalSerializationApi` and requires Kotlin **2.0.20** or higher.
- It's not allowed on classes involved in polymorphism (interfaces, sealed classes, abstract classes). Section 7 covers those.
- The delegate already applies `@SerialName`, defaults and nested serializers, so your code never touches properties directly.

### Respecting the `JsonNamingStrategy`

Once you post-process a `JsonObject`, you look keys up by name. If the `Json` instance uses a naming strategy such as
`JsonNamingStrategy.SnakeCase`, `retryCount` is written as `retry_count`, and a lookup on the Kotlin name silently misses. This helper
asks the strategy for the real key:

```kotlin
@OptIn(ExperimentalSerializationApi::class)
fun SerialDescriptor.jsonKey(json: Json, serialName: String) =
	json.configuration.namingStrategy?.serialNameForJson(this, getElementIndex(serialName), serialName) ?: serialName
```

A naming strategy can also honor your own annotations. Kore's strategy reads a `@JsonSerialName` annotation (marked `@SerialInfo`, so it
lands in the descriptor) before falling back to snake_case:

```kotlin
@OptIn(ExperimentalSerializationApi::class)
@SerialInfo
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
annotation class JsonSerialName(val name: String)

@OptIn(ExperimentalSerializationApi::class)
data object JsonNamingSnakeCaseStrategy : JsonNamingStrategy {
	override fun serialNameForJson(descriptor: SerialDescriptor, elementIndex: Int, serialName: String) =
		descriptor.getElementAnnotations(elementIndex).filterIsInstance<JsonSerialName>().firstOrNull()?.name
			?: JsonNamingStrategy.SnakeCase.serialNameForJson(descriptor, elementIndex, serialName)
}
```

---

## 5. Simplifying Property Serialization with InlineAutoSerializer

The `InlineAutoSerializer` serializes a single-property wrapper as its property value directly, like a value class, without the
limitations of actual inline classes: the wrapper can extend a sealed class, hold a `var` or have several constructors.

### Purpose

- **Property Serialization**: Serialize a wrapper as its only property, in both directions.
- **No Reflection**: A getter lambda and a constructor reference replace `KProperty1` and `createInstance()`.
- **More Flexible than Inline Classes**: Value classes can't extend classes, `InlineAutoSerializer` has no such limit.

### Usage Patterns

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class InlineAutoSerializer<T, P>(
	private val propertySerializer: KSerializer<P>,
	private val property: (T) -> P,
	private val factory: (P) -> T,
	serialName: String? = null,
) : KSerializer<T> {
	override val descriptor =
		if (serialName == null) propertySerializer.descriptor
		else SerialDescriptor(serialName, propertySerializer.descriptor)

	override fun serialize(encoder: Encoder, value: T) =
		encoder.encodeSerializableValue(propertySerializer, property(value))

	override fun deserialize(decoder: Decoder): T =
		factory(decoder.decodeSerializableValue(propertySerializer))
}
```

**Example Usage:**

```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Serializable(with = Storage.Companion.StorageSerializer::class)
data class Storage(val items: List<String> = emptyList()) {
	companion object {
		data object StorageSerializer : InlineAutoSerializer<Storage, List<String>>(
			ListSerializer(String.serializer()), Storage::items, ::Storage
		)
	}
}

println(Json.encodeToString(Storage(listOf("item1", "item2"))))
```

Resulting JSON:

```json
[
	"item1",
	"item2"
]
```

**Explanation:**

- **Serializer Implementation**: `Storage::items` reads the value, `::Storage` rebuilds the wrapper when decoding.
- **The `serialName` parameter**: `SerialDescriptor(name, original)` copies a descriptor under a new name. Two subtypes of the same sealed
  family wrapping the same type (say two `List<String>` wrappers) would otherwise share a serial name, and a lookup by serial name
  (section 7) couldn't tell them apart.

---

## 6. Optimizing JSON Structures

Many formats accept a short form and a long form of the same value. These serializers write the short form whenever it carries the same
information, and accept both when reading.

### SinglePropertySimplifierSerializer

Collapses an object to one property's value when it's the only property written:

```kotlin
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@OptIn(ExperimentalSerializationApi::class)
open class SinglePropertySimplifierSerializer<T : Any>(
	private val delegate: KSerializer<T>,
	private val propertyName: String,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	/** The value of the only entry of this object when its key is [key]. */
	private fun <E> Map<String, E>.singleValueAt(key: String) = entries.singleOrNull()?.takeIf { it.key == key }?.value

	override fun serialize(encoder: Encoder, value: T) = when (encoder) {
		is JsonEncoder -> {
			val obj = encoder.json.encodeToJsonElement(delegate, value).jsonObject
			encoder.encodeJsonElement(obj.singleValueAt(descriptor.jsonKey(encoder.json, propertyName)) ?: obj)
		}

		else -> encoder.encodeSerializableValue(delegate, value)
	}

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			val obj = element as? JsonObject ?: JsonObject(mapOf(descriptor.jsonKey(decoder.json, propertyName) to element))
			decoder.json.decodeFromJsonElement(delegate, obj)
		}

		else -> decoder.decodeSerializableValue(delegate)
	}
}
```

The `singleValueAt` helper works on any `Map<String, E>`, so Kore adds an `is NbtEncoder` branch doing the same on a knbt `NbtCompound`.

**Example Usage**, with the `Config` class from section 4:

```kotlin
println(Json.encodeToString(Config(enabled = true)))                 // true
println(Json.encodeToString(Config(enabled = true, retryCount = 3))) // {"enabled":true,"retryCount":3}
println(Json.decodeFromString<Config>("true"))                       // Config(enabled=true, retryCount=null)
```

`retryCount` is omitted from the first output because it equals its default and `encodeDefaults` is `false` by default, which leaves
`enabled` as the only key.

### EitherInlineSerializer

Some formats accept one of several properties written bare, like a target given as either a name or an id. `EitherInlineSerializer`
writes the first non-null listed property bare, and decodes a bare value by trying each property in turn:

```kotlin
@OptIn(ExperimentalSerializationApi::class)
open class EitherInlineSerializer<T : Any>(
	private val delegate: KSerializer<T>,
	private vararg val propertyNamesToInline: String,
) : KSerializer<T> {
	override val descriptor get() = delegate.descriptor

	override fun serialize(encoder: Encoder, value: T) = when (encoder) {
		is JsonEncoder -> {
			val obj = encoder.json.encodeToJsonElement(delegate, value).jsonObject
			val inlined = propertyNamesToInline.firstNotNullOfOrNull { obj[descriptor.jsonKey(encoder.json, it)] }
			encoder.encodeJsonElement(inlined ?: obj)
		}

		else -> encoder.encodeSerializableValue(delegate, value)
	}

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> {
			val element = decoder.decodeJsonElement()
			propertyNamesToInline.firstNotNullOfOrNull { name ->
				val wrapped = JsonObject(mapOf(descriptor.jsonKey(decoder.json, name) to element))
				runCatching { decoder.json.decodeFromJsonElement(delegate, wrapped) }.getOrNull()
			} ?: decoder.json.decodeFromJsonElement(delegate, element)
		}

		else -> decoder.decodeSerializableValue(delegate)
	}
}
```

**Example Usage:**

```kotlin
@OptIn(ExperimentalSerializationApi::class)
@KeepGeneratedSerializer
@Serializable(with = EntityTarget.Companion.EntityTargetSerializer::class)
data class EntityTarget(val name: String? = null, val id: Int? = null) {
	companion object {
		data object EntityTargetSerializer : EitherInlineSerializer<EntityTarget>(generatedSerializer(), "name", "id")
	}
}

Json.encodeToString(EntityTarget(name = "Steve")) // "Steve"
Json.encodeToString(EntityTarget(id = 42))        // 42
Json.decodeFromString<EntityTarget>("42")         // EntityTarget(name=null, id=42)
```

Decoding `42` first tries `{"name":42}`, which a non-lenient `Json` rejects since `name` is a string, then `{"id":42}`, which works.

### Transforming the Tree with `JsonTransformingSerializer`

For JSON-only rewrites, the built-in `JsonTransformingSerializer` gives you hooks on the tree. Kore writes a worldgen provider as its bare
value when its type is `minecraft:constant`:

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

open class ProviderSerializer<T : Any>(
	kSerializer: KSerializer<T>,
	private val constantName: String = "constant",
	private val typePropertyName: String = "type",
	private val valuePropertyName: String = "value",
) : JsonTransformingSerializer<T>(kSerializer) {
	override fun transformSerialize(element: JsonElement): JsonElement {
		val provider = element.jsonObject
		val isConstant = provider.getValue(typePropertyName).jsonPrimitive.content == "minecraft:$constantName"
		return if (isConstant) provider.getValue(valuePropertyName) else provider
	}
}
```

`{"type":"minecraft:constant","value":64}` becomes `64`, every other provider keeps its object. Override `transformDeserialize` the same
way to accept the short form when reading.

---

## 7. Polymorphic Serialization with Namespaces

The built-in polymorphism writes a class discriminator holding the serial name of the subtype, so you end up adding
`@SerialName("namespace:type")` to every subclass, which is tedious and error-prone. It also can't write a subtype that serializes to a
bare array or primitive, nor move the fields into a nested property. A custom serializer handles all of this, with no per-class
annotation.

### Dispatching Without Reflection: `SealedDispatcher`

Serializing a sealed family needs the serializer of the value's subtype. Reflection (`serializer(value::class.createType())`) is
JVM-only and slow, so the family gets a small object answering two questions:

```kotlin
import kotlinx.serialization.KSerializer

abstract class SealedDispatcher<T : Any>(val serialName: String) {
	/** The serializer of [value]'s subtype. */
	abstract fun serializerOf(value: T): KSerializer<T>

	/** Every subtype's serializer, in the order bare values are tried when decoding. */
	protected abstract fun serializers(): List<KSerializer<out T>>

	private val bySerialName = lazy {
		val serializers = serializers()
		serializers.associateBy { it.descriptor.serialName }.also { bySerialName ->
			check(bySerialName.size == serializers.size) {
				val duplicates = serializers.groupBy { it.descriptor.serialName }.filterValues { it.size > 1 }.keys
				"Several subtypes of '$serialName' share the serial names $duplicates."
			}
		}
	}

	/** Every subtype's serializer by serial name, loading the whole family on first access: only decoding needs it. */
	val serializersBySerialName: Map<String, KSerializer<out T>> get() = bySerialName.value
}
```

Writing these by hand for dozens of families would be its own maintenance problem, so KSP generates them (section 8).

### NamespacedPolymorphicSerializer

The serializer derives each subtype's name from its descriptor's serial name (`com.example.Shape.Circle` becomes `circle`, a
`@SerialName` wins when present), then writes it under `type` with a namespace. The content and wrapping logic works on any
`Map<String, E>`, so the same code handles a `JsonObject` and a knbt `NbtCompound`:

```kotlin
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.*

private val ACRONYM_BOUNDARY = Regex("([A-Z]+)([A-Z][a-z])")
private val LETTER_THEN_DIGIT = Regex("([a-z])([0-9])")
private val LOWER_THEN_UPPER = Regex("([a-z])([A-Z])")

fun String.snakeCase() = replace(ACRONYM_BOUNDARY, "$1_$2")
	.replace(LETTER_THEN_DIGIT, "$1_$2")
	.replace(LOWER_THEN_UPPER, "$1_$2")
	.lowercase()

fun defaultContentName(serialName: String) = serialName.substringAfterLast('.').snakeCase()

open class NamespacedPolymorphicSerializer<T : Any>(
	private val dispatcher: SealedDispatcher<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
	private val namespace: String? = "minecraft",
	private val skipEmptyOutput: Boolean = true,
	private val contentName: (String) -> String = ::defaultContentName,
) : KSerializer<T> {
	override val descriptor = serialDescriptor<JsonElement>()

	private val baseName get() = dispatcher.serialName
	private fun namespaced(name: String) = namespace?.let { "$it:$name" } ?: name
	private fun normalize(typeName: String) = namespace?.let { typeName.removePrefix("$it:") } ?: typeName

	private val serialNameByContent = lazy { dispatcher.serializersBySerialName.keys.associateBy(contentName) }

	private fun deserializerOf(typeName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContent.value[normalize(typeName)] ?: normalize(typeName)
		return dispatcher.serializersBySerialName[serialName] ?: error("No subtype '$typeName' in $baseName")
	}

	/** The fields of a serialized object, without the type name: at the top level or inside [moveIntoProperty]. */
	private fun <E> content(fields: Map<String, E>, asObject: (E) -> Map<String, E>?) = when (moveIntoProperty) {
		null -> fields - outputName
		else -> fields[moveIntoProperty]?.let(asObject).orEmpty()
	}

	/** The inverse of [content]: [fields] with [typeName] first, moved into [moveIntoProperty] when set. */
	private fun <E> wrap(fields: Map<String, E>, typeName: E, toObject: (Map<String, E>) -> E) = buildMap {
		if (!skipOutputName) put(outputName, typeName)
		when (moveIntoProperty) {
			null -> putAll(fields - outputName)
			else -> if (!(skipEmptyOutput && fields.isEmpty())) put(moveIntoProperty, toObject(fields - outputName))
		}
	}

	/** A bare (non-object) value has no discriminator to read, so every subtype is tried until one decodes it. */
	private inline fun decodeBare(value: Any, decode: (DeserializationStrategy<T>) -> T) =
		dispatcher.serializersBySerialName.values.firstNotNullOfOrNull { runCatching { decode(it) }.getOrNull() }
			?: error("No subtype of $baseName can deserialize non-object value: $value")

	override fun deserialize(decoder: Decoder): T = when (decoder) {
		is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
			is JsonObject -> {
				val typeName = element[outputName]?.jsonPrimitive?.content
					?: error("Missing '$outputName' field in JSON object for $baseName")
				decoder.json.decodeFromJsonElement(deserializerOf(typeName), JsonObject(content(element) { it as? JsonObject }))
			}

			else -> decodeBare(element) { decoder.json.decodeFromJsonElement(it, element) }
		}

		is NbtDecoder -> when (val tag = decoder.decodeNbtTag()) {
			is NbtCompound -> {
				val typeName = (tag[outputName] as? NbtString)?.value
					?: error("Missing '$outputName' field in NBT compound for $baseName")
				decoder.nbt.decodeFromNbtTag(deserializerOf(typeName), NbtCompound(content(tag) { it as? NbtCompound }))
			}

			else -> decodeBare(tag) { decoder.nbt.decodeFromNbtTag(it, tag) }
		}

		else -> error("$baseName can only be deserialized from JSON or NBT.")
	}

	override fun serialize(encoder: Encoder, value: T) {
		val actual = dispatcher.serializerOf(value)
		val outputClassName = namespaced(contentName(actual.descriptor.serialName))

		when (encoder) {
			is JsonEncoder -> encoder.encodeJsonElement(
				when (val body = encoder.json.encodeToJsonElement(actual, value)) {
					is JsonObject -> JsonObject(wrap(body, JsonPrimitive(outputClassName), ::JsonObject))
					else -> body
				}
			)

			is NbtEncoder -> encoder.encodeNbtTag(
				when (val body = encoder.nbt.encodeToNbtTag(actual, value)) {
					is NbtCompound -> NbtCompound(wrap(body, NbtString(outputClassName), ::NbtCompound))
					else -> body
				}
			)

			else -> error("$baseName can only be serialized to JSON or NBT.")
		}
	}
}
```

Kore's version hardcodes the `minecraft:` namespace behind a `useMinecraftPrefix` flag, the `namespace` parameter here generalizes it.

### Benefits

- **No Annotation Per Subclass**: Names come from the descriptor, `@SerialName` stays available as an override.
- **Bare Subtypes**: A subtype serializing to an array or a primitive is written as-is, and decoded by trying each subtype.
- **Format-Agnostic**: One wrapping logic for JSON and NBT, thanks to the generic `Map<String, E>` helpers.
- **Fails Fast**: Two subtypes sharing a serial name throw on first decode instead of silently picking one.

### Example Usage

Sealed classes and sealed interfaces both work:

```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@GeneratedSealedSerializer
@Serializable(with = Shape.Companion.ShapeSerializer::class)
sealed interface Shape {
	@Serializable
	data class Circle(val radius: Double) : Shape

	@Serializable
	data class Rectangle(val width: Double, val height: Double) : Shape

	companion object {
		data object ShapeSerializer : NamespacedPolymorphicSerializer<Shape>(shapeSealedSerializer(), namespace = "shapes")
	}
}

println(Json.encodeToString<Shape>(Shape.Circle(5.0)))
```

Encode through the base type (`encodeToString<Shape>`): with the static type `Circle`, the plugin picks `Circle.serializer()` and no
`type` is written. Resulting JSON:

```json
{
	"type": "shapes:circle",
	"radius": 5.0
}
```

You have many options to customize the serializer:

- `outputName`: The name of the key that will contain the namespace and the type.
- `skipOutputName`: If `true`, the `outputName` key won't be added.
- `moveIntoProperty`: If not `null`, the fields are moved into a property with the given name.
- `namespace`: The namespace added before the type, `null` for none.
- `skipEmptyOutput`: With `moveIntoProperty`, skips the nested property when the subtype has no fields.
- `contentName`: Maps a serial name to the written type name.

**Example with `moveIntoProperty`:**

```kotlin
data object ShapeSerializer : NamespacedPolymorphicSerializer<Shape>(
	shapeSealedSerializer(),
	moveIntoProperty = "shape",
	namespace = "shapes",
)
```

Resulting JSON:

```json
{
	"type": "shapes:circle",
	"shape": {
		"radius": 5.0
	}
}
```

Kore's advancement triggers use exactly this, with `outputName = "trigger"` and `moveIntoProperty = "conditions"`.

### Enum-Like Sealed Families

Some formats mix bare names and typed objects in the same field: `"override"` for a case without options,
`{"type": "blend_to_gray", "brightness": 0.5, "factor": 1.0}` for one with options. A sealed family models it with `data object` and
`data class` leaves, and `EnumLikeSerializer` writes each leaf in the right shape:

```kotlin
open class EnumLikeSerializer<T : Any>(private val dispatcher: SealedDispatcher<T>) : KSerializer<T> {
	private val baseName get() = dispatcher.serialName

	override val descriptor = PrimitiveSerialDescriptor(baseName, PrimitiveKind.STRING)

	private val serialNameByContentName = lazy { dispatcher.serializersBySerialName.keys.associateBy(::defaultContentName) }

	private fun caseSerializer(contentName: String): DeserializationStrategy<T> {
		val serialName = serialNameByContentName.value[contentName] ?: contentName
		return dispatcher.serializersBySerialName[serialName] ?: error("No case '$contentName' in $baseName")
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder) { "$baseName can only be serialized as JSON." }

		val actual = dispatcher.serializerOf(value)
		val name = defaultContentName(actual.descriptor.serialName)
		val body = encoder.json.encodeToJsonElement(actual, value).jsonObject

		if (body.isEmpty()) encoder.encodeString(name)
		else encoder.encodeJsonElement(JsonObject(mapOf<String, JsonElement>("type" to JsonPrimitive(name)) + (body - "type")))
	}

	override fun deserialize(decoder: Decoder): T {
		require(decoder is JsonDecoder) { "$baseName can only be deserialized from JSON." }

		return when (val element = decoder.decodeJsonElement()) {
			is JsonObject -> decoder.json.decodeFromJsonElement(
				caseSerializer(element.getValue("type").jsonPrimitive.content),
				JsonObject(element - "type"),
			)

			else -> decoder.json.decodeFromJsonElement(caseSerializer(element.jsonPrimitive.content), JsonObject(emptyMap()))
		}
	}
}
```

```kotlin
@GeneratedSealedSerializer
@Serializable(with = Modifier.Companion.ModifierSerializer::class)
sealed interface Modifier {
	@Serializable
	data object Override : Modifier

	@Serializable
	data class BlendToGray(val brightness: Float, val factor: Float) : Modifier

	companion object {
		data object ModifierSerializer : EnumLikeSerializer<Modifier>(modifierSealedSerializer())
	}
}

Json.encodeToString<Modifier>(Modifier.Override)              // "override"
Json.encodeToString<Modifier>(Modifier.BlendToGray(0.5f, 1f)) // {"type":"blend_to_gray","brightness":0.5,"factor":1.0}
```

A `data object` serializes to `{}`, which is how the serializer tells a bare case from a configurable one.

---

## 8. Generating Polymorphic Serializers with KSP

`@GeneratedSealedSerializer` is a plain marker annotation with `SOURCE` retention:

```kotlin
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class GeneratedSealedSerializer
```

A KSP processor finds every annotated sealed type, walks `getSealedSubclasses()` down to the concrete leaves (deduplicated, so a diamond
hierarchy emits each leaf once), and writes a `fooSealedSerializer()` factory next to it. Here's the heart of the generated file for
Kore's 58 advancement triggers:

```kotlin
// GENERATED by kore-ksp. Do not edit.
package io.github.ayfri.kore.features.advancements.triggers

@Suppress("UNCHECKED_CAST")
internal fun advancementTriggerConditionSealedSerializer(): SealedDispatcher<AdvancementTriggerCondition> =
	object : SealedDispatcher<AdvancementTriggerCondition>("AdvancementTriggerCondition") {
		override fun serializerOf(value: AdvancementTriggerCondition) = when (subtypeName(value)) {
			"AllayDropItemOnBlock" -> AllayDropItemOnBlock.serializer()
			"AnyBlockUse" -> AnyBlockUse.serializer()
			"AvoidVibration" -> AvoidVibration.serializer()
			// ... one branch per trigger
			else -> when (value) {
				is AllayDropItemOnBlock -> AllayDropItemOnBlock.serializer()
				is AnyBlockUse -> AnyBlockUse.serializer()
				// ...
			}
		} as KSerializer<AdvancementTriggerCondition>

		override fun serializers(): List<KSerializer<out AdvancementTriggerCondition>> = listOf(
			AllayDropItemOnBlock.serializer(),
			AnyBlockUse.serializer(),
			// ...
		)
	}
```

The processor itself is mostly string building. Its key parts:

```kotlin
class SealedSerializerProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
	private var processed = false

	override fun process(resolver: Resolver): List<KSAnnotated> {
		// Everything is emitted in the first round, so later rounds would only rescan the generated files.
		if (processed) return emptyList()
		processed = true

		for (symbol in resolver.getSymbolsWithAnnotation("com.example.GeneratedSealedSerializer")) {
			if (symbol !is KSClassDeclaration || Modifier.SEALED !in symbol.modifiers) {
				logger.error("@GeneratedSealedSerializer can only be applied to a sealed class/interface.", symbol)
				continue
			}
			generateFactory(symbol)
		}
		return emptyList()
	}

	private fun leavesOf(declaration: KSClassDeclaration): List<KSClassDeclaration> {
		val direct = declaration.getSealedSubclasses().toList()
		return if (direct.isEmpty()) listOf(declaration) else direct.flatMap(::leavesOf)
	}

	// generateFactory() sorts the leaves, appends the `when` branches shown above with buildString,
	// and writes the file with codeGenerator.createNewFile(Dependencies(false, *sourceFiles), packageName, fileName)
}
```

Passing the source files of the base type and every leaf to `Dependencies` keeps KSP incremental: editing one trigger only regenerates
its family's file.

### Why a `when` on the Simple Name

The usual generated code is a `SealedClassSerializer`, the serializer the plugin itself builds for sealed classes. It takes the array of
every subtype serializer in its constructor, so the first use of the family creates all of them and loads all their classes, even to
write a single subtype. Switching to a dispatcher that only creates the serializer it needs took the first advancement trigger from
**61 ms and 186 loaded classes to 4 ms and 9 classes** on a cold JVM ([c92ceb4](https://github.com/Ayfri/Kore/commit/c92ceb4e)).

Inside the dispatcher, a `when` on the simple name beats an `is` chain: on the JVM, every `is Foo` check needs the class `Foo` loaded,
so reaching the last branch of a 58-case chain loads 58 classes, while a `when` on a string only loads the class of the branch it takes.

The simple name comes from an `expect`/`actual` function, since the cheapest call differs per platform:

```kotlin
// commonMain
internal expect fun subtypeName(value: Any): String?

// jvmMain: Class.getSimpleName, KClass.simpleName makes two native calls there
internal actual fun subtypeName(value: Any): String? = value.javaClass.simpleName

// jsMain
internal actual fun subtypeName(value: Any): String? = value::class.simpleName
```

The `is` chain stays as the `else` branch for two cases: homonym subtypes (two leaves named `Item` in different packages get no name
branch) and minified production JS, where class names no longer match the source.

### Open Hierarchies: Generated Serializer Maps

For a base type that isn't sealed (user code can add subtypes), a `@GeneratedSerializerMap` processor walks every class in the
compilation, keeps the concrete subtypes of the base, and emits a plain map:

```kotlin
// GENERATED by kore-ksp. Do not edit.
internal val componentSerializers: Map<KClass<out Component>, KSerializer<out Component>> = mapOf(
	CatCollar::class to CatCollar.serializer(),
	CatVariant::class to CatVariant.serializer(),
	// ...
)
```

A hand-written lookup next to it falls back to a base serializer for user subtypes the processor can't see:

```kotlin
@Suppress("UNCHECKED_CAST")
internal fun componentSerializerFor(value: Component) = (componentSerializers[value::class] ?: when (value) {
	is CustomComponent -> CustomComponent.Companion.CustomComponentSerializer
	else -> error("No serializer for component ${value::class.simpleName}, extend CustomComponent for components Kore doesn't ship.")
}) as KSerializer<Component>
```

A map lookup on `value::class` replaces `serializer(value::class.createType())`, and works on JS. Unlike a `SealedDispatcher`,
building this map creates every serializer in it (section 11 explains why that costs), which is the trade-off for a hierarchy with no
closed list of subtypes to switch on. The processor resolves supertypes
through a shared cache, so a hierarchy common to many candidates resolves once instead of once per candidate.

---

## 9. Handling Complex Data Types

Complex data types like tuples or lists of mixed types require specialized serializers to keep a compact, readable output.

### TripleAsArraySerializer

Serializes Kotlin `Triple` objects as JSON arrays. A list descriptor normally has one element type, so a small custom descriptor gives
each index its own:

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind

@OptIn(SealedSerializationApi::class)
data class MixedListSerialDescriptor(private val elementTypes: List<KSerializer<*>>) : SerialDescriptor {
	override val elementsCount = elementTypes.size
	override val kind = StructureKind.LIST
	override val serialName = "MixedListType"

	override fun getElementAnnotations(index: Int) = emptyList<Annotation>()
	override fun getElementDescriptor(index: Int) = elementTypes[index].descriptor
	override fun getElementIndex(name: String) = name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")
	override fun getElementName(index: Int) = index.toString()
	override fun isElementOptional(index: Int) = false
}

fun mixedListSerialDescriptor(vararg elementTypes: KSerializer<*>) = MixedListSerialDescriptor(elementTypes.toList())
```

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.*

typealias TripleAsArray<A, B, C> = @Serializable(TripleAsArraySerializer::class) Triple<A, B, C>

class TripleAsArraySerializer<A, B, C>(
	private val firstSerializer: KSerializer<A>,
	private val secondSerializer: KSerializer<B>,
	private val thirdSerializer: KSerializer<C>,
) : KSerializer<Triple<A, B, C>> {
	override val descriptor = mixedListSerialDescriptor(firstSerializer, secondSerializer, thirdSerializer)

	@Suppress("UNCHECKED_CAST")
	override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
		var first = null as A
		var second = null as B
		var third = null as C
		while (true) {
			when (val index = decodeElementIndex(descriptor)) {
				0 -> first = decodeSerializableElement(descriptor, 0, firstSerializer)
				1 -> second = decodeSerializableElement(descriptor, 1, secondSerializer)
				2 -> third = decodeSerializableElement(descriptor, 2, thirdSerializer)
				CompositeDecoder.DECODE_DONE -> break
				else -> error("Unexpected index $index in TripleAsArray")
			}
		}
		Triple(first, second, third)
	}

	override fun serialize(encoder: Encoder, value: Triple<A, B, C>) =
		encoder.encodeCollection(descriptor, 3) {
			encodeSerializableElement(descriptor, 0, firstSerializer, value.first)
			encodeSerializableElement(descriptor, 1, secondSerializer, value.second)
			encodeSerializableElement(descriptor, 2, thirdSerializer, value.third)
		}
}
```

The plugin passes the serializers of `A`, `B` and `C` to the constructor, so the typealias works with any element types.

**Example Usage:**

```kotlin
@Serializable
data class Coordinates(val position: TripleAsArray<Double, Double, Double>)

println(Json.encodeToString(Coordinates(Triple(1.0, 2.0, 3.0))))
```

Resulting JSON:

```json
{
	"position": [1.0, 2.0, 3.0]
}
```

### InlinableListSerializer

Writes a single-element list as its element, and reads both forms back:

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder

typealias InlinableList<T> = @Serializable(with = InlinableListSerializer::class) List<T>

open class InlinableListSerializer<T>(private val kSerializer: KSerializer<T>) : KSerializer<List<T>> {
	private val listSerializer = ListSerializer(kSerializer)
	override val descriptor = listSerializer.descriptor

	override fun deserialize(decoder: Decoder) = when (decoder) {
		is JsonDecoder -> when (val element = decoder.decodeJsonElement()) {
			is JsonArray -> element.map { decoder.json.decodeFromJsonElement(kSerializer, it) }
			else -> listOf(decoder.json.decodeFromJsonElement(kSerializer, element))
		}

		else -> decoder.decodeSerializableValue(listSerializer)
	}

	override fun serialize(encoder: Encoder, value: List<T>) = when (value.size) {
		1 -> encoder.encodeSerializableValue(kSerializer, value[0])
		else -> encoder.encodeSerializableValue(listSerializer, value)
	}
}
```

The `ListSerializer` is built once in the constructor instead of on every call.

**Usage Example:**

```kotlin
@Serializable
data class UserRoles(val roles: InlinableList<String> = emptyList())

println(Json.encodeToString(UserRoles(listOf("admin", "user")))) // {"roles":["admin","user"]}
println(Json.encodeToString(UserRoles(listOf("admin"))))         // {"roles":"admin"}
println(Json.decodeFromString<UserRoles>("{\"roles\":\"admin\"}"))  // UserRoles(roles=[admin])
```

### Bridging Two Formats: NbtAsJsonSerializer

A raw NBT value (`NbtTag`) inside a class that's also written as JSON needs a serializer that speaks both. With an NBT encoder, it writes
the tag as-is, with a JSON encoder it converts the tag to a `JsonElement`. The reverse conversion has to guess tag types, and NBT lists
hold a single tag type, so only a mixed array falls back to strings:

```kotlin
private fun JsonElement.toNbtTag(): NbtTag = when (this) {
	is JsonObject -> NbtCompound(mapValues { it.value.toNbtTag() })
	is JsonArray -> map { it.toNbtTag() }.let { elements ->
		if (elements.distinctBy { it::class }.size <= 1) NbtList.of(*elements.toTypedArray())
		else NbtList(elements.map { it as? NbtString ?: NbtString(it.toString()) })
	}

	is JsonPrimitive -> when {
		isString -> NbtString(content)
		else -> booleanOrNull?.let(::NbtByte)
			?: longOrNull?.let { if (it in Int.MIN_VALUE..Int.MAX_VALUE) NbtInt(it.toInt()) else NbtLong(it) }
			?: doubleOrNull?.let(::NbtDouble)
			?: NbtString(content)
	}
}
```

`[1,2]` stays a list of ints, `[1,"a"]` becomes a list of strings.

---

## 10. Breaking Initialization Cycles with LazySerializer

A serializer declared as a `data object` builds its state when its class initializes. If class `A`'s serializer reads
`B.serializer().descriptor` while `B`'s serializer reads `A`'s, one of them reads the other while it's still initializing, gets a `null`
field, and the JVM wraps the failure in an `ExceptionInInitializerError`. A type recursing into itself is fine, the plugin-generated
serializer already handles that lazily, the problem only appears between two different classes.

The fix is to give the descriptor upfront and build the real serializer on first use:

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class LazySerializer<T>(override val descriptor: SerialDescriptor, provider: () -> KSerializer<T>) : KSerializer<T> {
	private val delegate = lazy(provider)
	override fun serialize(encoder: Encoder, value: T) = delegate.value.serialize(encoder, value)
	override fun deserialize(decoder: Decoder): T = delegate.value.deserialize(decoder)
}
```

**Usage Example**, an entity predicate that matches its passenger with another entity predicate:

```kotlin
@Serializable(with = PassengerSubPredicate.Companion.Serializer::class)
data class PassengerSubPredicate(val entity: EntityPredicate) : EntitySubPredicate() {
	companion object {
		data object Serializer : KSerializer<PassengerSubPredicate> by LazySerializer(
			buildClassSerialDescriptor("PassengerSubPredicate"),
			{ InlineAutoSerializer(EntityPredicate.serializer(), PassengerSubPredicate::entity, ::PassengerSubPredicate) },
		)
	}
}
```

Note `delegate.value` instead of `by lazy`: section 11 explains why.

---

## 11. Performance Optimization: Measured Results

Serialization in a DSL library is rarely a throughput problem: it's the **cold start**, the first call that initializes serializers and
loads classes. Every number below comes from a Kore commit, with its measurement method.

| Change | Before | After | Measured on |
|---|---|---|---|
| Sealed family: generated `SealedDispatcher` instead of a `SealedClassSerializer` creating every subtype serializer ([c92ceb4](https://github.com/Ayfri/Kore/commit/c92ceb4e)) | 61 ms, 186 classes | 4 ms, 9 classes | First advancement trigger, cold JVM |
| Same change, particles | 18 ms | 9 ms | Cold JVM |
| Same change, actions | 16 ms | 9 ms | Cold JVM |
| Generated `when` instead of a map of 148 factory lambdas ([67fcde6](https://github.com/Ayfri/Kore/commit/67fcde68)) | 101 ms | 3 ms | First tag of a pack, cold JVM |
| Lazy `Json` and a `when` instead of a map of every chat component serializer ([96e2357](https://github.com/Ayfri/Kore/commit/96e23573)) | 76 ms | 24 ms | First text component, cold JVM |
| Selector arguments rendered directly instead of through a JSON tree escaped then unescaped ([2a7336e](https://github.com/Ayfri/Kore/commit/2a7336e8)) | 111 ms, ~1.3 µs per render | 45 ms, ~0.5 µs per render | First selector, cold JVM |
| Selector options rendered directly instead of through a JSON tree ([1891fba](https://github.com/Ayfri/Kore/commit/1891fbac)) | 163 ms | 53 ms | First selector, cold JVM |
| Single-value `AbstractEncoder` instead of `encodeToJsonElement` ([db088a3](https://github.com/Ayfri/Kore/commit/db088a38)) | 186 ns per call (stdev 16 ns) | 116 ns per call (stdev 9 ns) | Median of 15 warm JVM rounds |
| `lazy(...)` read through `.value` instead of a `by lazy` delegate ([bdee8ce](https://github.com/Ayfri/Kore/commit/bdee8ce3)) | 40 ms | 12 ms | `LazySerializer` decoding 20,000 values, Kotlin/JS |
| No serializer on ~1,300 generated enums nothing serializes ([2bf85a5](https://github.com/Ayfri/Kore/commit/2bf85a54)) | 9,706 classes, 24.4 MB dev JS library, 71.7 s JVM compile | 8,387 classes, 23.4 MB dev JS library, 60.4 s JVM compile | Clean builds, compile times averaged |

### Load Only What You Serialize

`mapOf("text" to TextComponent.serializer(), ...)` calls every `serializer()` when the map is built, which initializes every serializer
and loads every class in it, even if you only ever write one type. A `when` does the same job lazily:

```kotlin
private fun serializerForType(type: String): KSerializer<out ChatComponent> = when (type) {
	"keybind" -> KeybindComponent.serializer()
	"nbt" -> NbtComponent.serializer()
	"text" -> PlainTextComponent.serializer()
	"translatable" -> TranslatedTextComponent.serializer()
	else -> error("Unknown chat component type: '$type'.")
}
```

The same goes for a `SealedDispatcher`: `serializerOf` loads one subtype, and only decoding reaches `serializersBySerialName`, which
loads the whole family.

### Don't Round-Trip Through a Tree

`encodeToJsonElement` followed by `encodeJsonElement` builds a whole `JsonElement` tree only to walk it again. When a serializer doesn't
need to inspect the output, delegate straight to the encoder:

```kotlin
// Builds a tree, then writes it
val element = encoder.json.encodeToJsonElement(serializer, value)
encoder.encodeJsonElement(element)

// Writes directly, works with any format
encoder.encodeSerializableValue(serializer, value)
```

The same applies to string rendering: building a `JsonObject` to format a command argument, then unescaping the result, costs more than
appending to a `StringBuilder`.

### Read a Single Value with a Custom `AbstractEncoder`

To get the serialized name of an enum or a value class as a `String`, `Json.encodeToJsonElement(value).jsonPrimitive.content` builds a
tree for one value. A tiny encoder keeps it instead:

```kotlin
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.EmptySerializersModule

/** Keeps the single value a serializer writes, so reading a value's serialized name builds no JSON tree. */
@OptIn(ExperimentalSerializationApi::class)
private class SingleValueEncoder : AbstractEncoder() {
	override val serializersModule = EmptySerializersModule()
	var result = ""

	override fun beginStructure(descriptor: SerialDescriptor) =
		throw SerializationException("${descriptor.serialName} doesn't serialize to a single value.")

	override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
		result = enumDescriptor.getElementName(index)
	}

	override fun encodeValue(value: Any) {
		result = value.toString()
	}
}

fun <T> serialNameOf(serializer: SerializationStrategy<T>, value: T) =
	SingleValueEncoder().apply { encodeSerializableValue(serializer, value) }.result
```

`encodeEnum` stores the entry's serial name, so `@SerialName` on enum entries is respected.

### Reuse Your `Json` Instances

`Json { ... }` is not a cheap value to rebuild in a getter. Each instance owns a schema cache, and with a naming strategy, the
name maps computed per descriptor live in that cache: a fresh `Json` per call recomputes them every time. Build it once, or cache it
behind the settings it depends on:

```kotlin
private var jsonCacheKey: Pair<Boolean, String>? = null
private lateinit var jsonCache: Json

val jsonEncoder: Json
	get() {
		val key = configuration.prettyPrint to configuration.prettyPrintIndent
		if (jsonCacheKey != key) {
			jsonCacheKey = key
			jsonCache = Json {
				prettyPrint = configuration.prettyPrint
				if (prettyPrint) prettyPrintIndent = configuration.prettyPrintIndent
				encodeDefaults = true
				explicitNulls = false
				namingStrategy = JsonNamingSnakeCaseStrategy
			}
		}
		return jsonCache
	}
```

When a `Json` is only needed on some paths, wrap it in a top-level `lazy` so the first unrelated call doesn't pay for it.

### `by lazy` on Kotlin/JS

On Kotlin/JS, each read of a `by lazy` property built a `KProperty` object in Kore's measurements, since a delegated property passes
its metadata to `getValue`. Holding the `Lazy` and reading `.value` skips it, which took `LazySerializer` decoding from 40 ms to 12 ms on
20,000 values:

```kotlin
// One KProperty per read on Kotlin/JS
private val delegate by lazy(provider)

// Direct field read
private val delegate = lazy(provider)
fun use() = delegate.value
```

### Smaller Wins That Add Up

- **Lookup maps built once**: `values.first { it.name.lowercase() == value }` scans the entries on every decode, a map built on first use
  doesn't (see `EnumStringSerializer`).
- **Hoisted regexes**: a `Regex(...)` inside `snakeCase()` compiles the pattern on every call. Top-level `val`s compile it once, which
  matters when every polymorphic type name goes through it.
- **Fewer generated serializers**: every `@Serializable` class costs a generated serializer class. Don't annotate types you never
  serialize, it shrinks the binary and the compile time (the last row of the table).
- **Measure cold and warm separately**: a cold JVM run shows class loading and initialization, warm rounds with several samples and their
  variance show the steady state. They point to different fixes.

---

## 12. Best Practices and Performance Tips

### Serializer Reusability

- **Generic Base Classes**: Write `open class FooSerializer<T>(...)` once, then declare one `data object` per type. Kore has a handful of
  base serializers for hundreds of classes.
- **Delegate, Don't Rebuild**: Wrap `generatedSerializer()` and post-process its output instead of re-encoding properties by hand.
- **Check Before Writing a New One**: Most new shapes are an existing serializer with another parameter.

### Multiplatform Compatibility

- **No `kotlin-reflect`**: `memberProperties`, `createType()`, `isAccessible` and `createInstance()` are JVM-only. Use getters,
  constructor references and generated code instead.
- **`expect`/`actual` for Hot Paths**: When the cheapest call differs per platform, like the class simple name, split it.
- **Watch Minification**: Class names on minified JS don't match the source, keep a fallback for anything keyed on them.

### Error Handling

- **Descriptive Errors**: Name the type and the bad value, like `'foo' is not an entry of this enum.` or `No subtype 'foo' in Shape`.
- **Throw `SerializationException`**: Callers catching serialization failures expect it, not a `NoSuchElementException` from `first {}`.
- **Fail at the Source**: Check invariants like unique serial names where the data is built, not where it's used.

### Testing

Test every custom serializer both ways, on the exact output shape:

```kotlin
@Test
fun configSimplifiesToItsOnlyProperty() {
	val config = Config(enabled = true)
	val json = Json.encodeToString(config)

	assertEquals("true", json)
	assertEquals(config, Json.decodeFromString<Config>(json))
}
```

---

## 13. FAQ

### Does kotlinx.serialization use reflection?

Not by itself: the compiler plugin generates serializers at build time. Reflection comes from your own code, like `kotlin-reflect` calls
in a custom serializer. Those don't exist on Kotlin/JS or Native, which is why this guide replaces them with `generatedSerializer()`,
getters, constructor references and KSP.

### How do I serialize a sealed interface without a class discriminator?

For a plain object without any type field, set `classDiscriminatorMode = ClassDiscriminatorMode.NONE` on your `Json`, keeping in mind
kotlinx.serialization can't decode polymorphic classes written in this mode. For a custom type field (namespaced names, bare subtypes, nested content), use a custom serializer like the
`NamespacedPolymorphicSerializer` of section 7.

### How do I keep the generated serializer when I write a custom one?

Annotate the class with `@KeepGeneratedSerializer` next to `@Serializable(with = ...)`, then call `generatedSerializer()` from the
companion. It requires Kotlin 2.0.20 or higher and isn't allowed on sealed, abstract or polymorphic classes.

### Why is my first serialization call so slow?

The first call initializes serializers and loads classes. Look for maps or lists that build every serializer upfront, `is` chains over
large sealed families and `Json` instances rebuilt on every call (section 11).

### How do I fix an `ExceptionInInitializerError` thrown by a serializer?

It usually means two classes' serializers need each other during class initialization. Give one of them a `LazySerializer` with an
explicit descriptor (section 10).

### How do I write one serializer for JSON and another format?

Check the encoder type: `is JsonEncoder` gives you `encoder.json`, `is NbtEncoder` (knbt) gives you `encoder.nbt`, and an `else` branch
delegates with `encodeSerializableValue`. Keep the logic on `Map<String, E>` and both formats share it.

---

## 14. Conclusion

Mastering **kotlinx.serialization** empowers you to handle complex serialization scenarios with ease and efficiency. Delegating to the
generated serializer keeps custom serializers short and reflection-free, KSP replaces what reflection would compute at runtime, and a few
habits (load only what you serialize, skip intermediate trees, reuse `Json` instances) keep the first call fast.

### Next Steps

- **Explore More**: Dive deeper into the [kotlinx.serialization guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md)
  to uncover additional features and capabilities.
- **Experiment**: Apply the discussed techniques in your projects, measure cold and warm, and keep only what the numbers support.
- **Contribute**: Engage with the Kotlin community to share insights, seek feedback, and contribute to the ongoing development of
  serialization tools.

---

## Appendix

### Thanks

- [ephemient](https://github.com/ephemient)
- [Ben Woodworth](https://github.com/BenWoodworth)
- [Adam S](https://kotlinlang.slack.com/team/U01681FLZC2)

For their help and insights on the subject.

### Reference Implementation

All in [Kore's serializers package](https://github.com/Ayfri/Kore/tree/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers):

- **Enum Serializers**:
  [SerializationUtils.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/utils/SerializationUtils.kt) (`EnumStringSerializer`),
  [UppercaseSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/UppercaseSerializer.kt),
  [LowercaseSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/LowercaseSerializer.kt)
- **Shape Serializers**:
  [InlineAutoSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/InlineAutoSerializer.kt),
  [SinglePropertySimplifierSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/SinglePropertySimplifierSerializer.kt),
  [EitherInlineSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/EitherInlineSerializer.kt),
  [ProviderSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/ProviderSerializer.kt)
- **Polymorphic Serializers**:
  [NamespacedPolymorphicSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/NamespacedPolymorphicSerializer.kt),
  [EnumLikeSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/EnumLikeSerializer.kt),
  [SealedDispatcher.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/SealedDispatcher.kt)
- **KSP Processors**:
  [SealedSerializerProcessor.kt](https://github.com/Ayfri/Kore/blob/master/kore-ksp/src/main/kotlin/io/github/ayfri/kore/ksp/SealedSerializerProcessor.kt),
  [SerializerMapProcessor.kt](https://github.com/Ayfri/Kore/blob/master/kore-ksp/src/main/kotlin/io/github/ayfri/kore/ksp/SerializerMapProcessor.kt)
- **Complex Types**:
  [TripleAsArraySerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/TripleAsArraySerializer.kt),
  [InlinableListSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/InlinableListSerializer.kt),
  [NbtAsJsonSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/NbtAsJsonSerializer.kt),
  [LazySerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/commonMain/kotlin/io/github/ayfri/kore/serializers/LazySerializer.kt)

### Further Reading

- [kotlinx.serialization GitHub Repository](https://github.com/Kotlin/kotlinx.serialization)
- [Official Kotlin Serialization Documentation](https://kotlinlang.org/docs/serialization.html)
- [Custom serializers guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serializers.md)
- [Polymorphism guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md)
- [JSON features guide](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md)
- [KSP overview](https://kotlinlang.org/docs/ksp-overview.html)
