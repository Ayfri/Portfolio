---
nav-title: Mastering kotlinx.serialization
title: "Mastering kotlinx.serialization: Advanced Techniques and Tricks"
description: Explore advanced techniques and tricks for mastering kotlinx.serialization in Kotlin, including custom serializers, enum serialization strategies, polymorphic serialization, and performance optimization.
keywords: kotlinx.serialization, kotlin serialization, custom serializers, enum serialization, polymorphic serialization, performance optimization
date-created: 2024-12-01
date-modified: 2024-12-01
root: .layouts.ArticleLayout
routeOverride: /articles/kotlin-advanced-serialization/index
---

Explore the powerful features of [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) with this comprehensive guide.
Dive into advanced serialization techniques, custom serializers, and smart strategies to optimize your Kotlin projects.

---

## 1. Introduction to kotlinx.serialization

**kotlinx.serialization** is a robust, multiplatform serialization library for Kotlin, designed to effortlessly convert Kotlin objects to
and from various data formats such as JSON, ProtoBuf, CBOR, and more. Its seamless integration with Kotlin's language features makes it a
preferred choice for developers aiming for clean and efficient serialization mechanisms.

### Why Advanced Techniques?

While the default serialization capabilities are sufficient for many use cases, complex projects often demand custom serialization
strategies to handle specific requirements like polymorphism, custom naming conventions, or performance optimizations. Mastering these
advanced techniques ensures your serialization logic remains maintainable, efficient, and tailored to your application's needs.

In the Kore project, these advanced serialization techniques are extensively utilized to enhance the efficiency and flexibility of data
handling. By leveraging custom serializers and polymorphic serialization, Kore ensures robust and maintainable serialization logic tailored
to its specific needs. You can explore more about the Kore project at [kore.ayfri.com](https://kore.ayfri.com) or visit the GitHub
repository at [github.com/Ayfri/Kore](https://github.com/Ayfri/Kore) for further insights and contributions.

## 2. Crafting Custom Serializers

Custom serializers allow you to override the default serialization behavior, providing fine-grained control over how your objects are
serialized and deserialized.

### When to Customize

- **Non-Standard Data Formats**: When dealing with APIs that expect a specific data structure not directly supported by default serializers.
- **Performance Optimizations**: To optimize serialization for large objects or performance-critical applications.
- **Polymorphic Serialization**: Handling complex inheritance hierarchies and ensuring type safety during serialization.

### Implementing `KSerializer`

Creating a custom serializer involves implementing the `KSerializer` interface. Here's a step-by-step guide:

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Locale

private fun String.camelcase(): String {
	val words = lowercase().split("_")
	return words[0] + words.drop(1).joinToString("") { word ->
		word.replaceFirstChar { it.titlecase(Locale.ENGLISH) }
	}
}

open class CamelcaseSerializer<T>(private val values: EnumEntries<T>) : KSerializer<T> where T : Enum<T> {
	override val descriptor = PrimitiveSerialDescriptor("CamelcaseSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeString()
		return values.first { it.name.camelcase() == value }
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.name.camelcase())
}
```

**Explanation:**

- **Descriptor**: Defines the structure of the serialized form.
- **serialize**: Converts the enum name to camelCase before serialization.
- **deserialize**: Reverts the camelCase string back to the enum instance.

---

## 3. Enum Serialization Strategies

Enums often require specific serialization formats to align with backend expectations or external APIs. **kotlinx.serialization** offers
flexible strategies to handle various enum serialization needs.

### UppercaseSerializer

Serializes enum values to uppercase strings.

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class UppercaseSerializer<T>(private val values: EnumEntries<T>) : KSerializer<T> where T : Enum<T> {
	override val descriptor = PrimitiveSerialDescriptor("UppercaseSerializer", PrimitiveKind.STRING)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeString()
		return values.first { it.name.uppercase() == value }
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.name.uppercase())
}
```

**Usage Example:**

```kotlin
import kotlinx.serialization.Serializable

@Serializable(with = Status.StatusSerializer::class)
enum class Status {
	ACTIVE,
	INACTIVE,
	PENDING;

	data object StatusSerializer : UppercaseSerializer<Status>(entries)
}

@Serializable
data class User(
	val status: Status
)

val user = User(Status.ACTIVE)
val json = Json.encodeToString(user)
println(json)
```

Resulting JSON:

```json
{
	"status": "ACTIVE"
}
```

### EnumOrdinalSerializer

Enumerations have a predefined order based on their declaration sequence. The `EnumOrdinalSerializer` serializes enums based on their
ordinal values.

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

open class EnumOrdinalSerializer<T>(private val values: EnumEntries<T>) : KSerializer<T> where T : Enum<T> {
	override val descriptor = PrimitiveSerialDescriptor("EnumOrdinalSerializer", PrimitiveKind.INT)

	override fun deserialize(decoder: Decoder): T {
		val value = decoder.decodeInt()
		return values[value]
	}

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeInt(value.ordinal)
}
```

**Usage Example:**

```kotlin
import kotlinx.serialization.Serializable

@Serializable(with = Status.StatusSerializer::class)
enum class Status {
	ACTIVE,
	INACTIVE,
	PENDING;

	data object StatusSerializer : EnumOrdinalSerializer<Status>(values())
}

@Serializable
data class User(
	val status: Status
)

val user = User(Status.ACTIVE)
val json = Json.encodeToString(user)
println(json)
```

Resulting JSON:

```json
{
	"status": 0
}
```

---

## 4. Simplifying Property Serialization with InlineSerializer

The `InlineSerializer` simulates the serialization process of a value class without the limitations of actual inline classes. It allows you
to serialize a property directly without wrapping it in a class.

### Purpose

- **Property Serialization**: Serialize a property directly without creating a separate class.
- **Custom Serialization Logic**: Implement custom serialization logic for specific properties.
- **More Flexible than Inline Classes**: Inline classes have limitations, such as not being able to implement interfaces or extend classes,
  but `InlineSerializer` doesn't have these limitations.

### Usage Patterns

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KProperty1

open class InlineSerializer<T, P>(
	private val kSerializer: KSerializer<in P>,
	private val property: KProperty1<T, P>,
) : KSerializer<T> {
	override val descriptor = NbtTag.serializer().descriptor

	override fun deserialize(decoder: Decoder) = error("InlineSerializer cannot be deserialized")

	override fun serialize(encoder: Encoder, value: T) = encoder.encodeSerializableValue(kSerializer, property.get(value))
}
```

**Example Usage:**

```kotlin
import kotlinx.serialization.Serializable

@Serializable(with = Storage.StorageSerializer::class)
data class Storage(
	val list: List<String> = emptyList(),
) {
	data object StorageSerializer : InlineSerializer<Storage, String>(String.serializer(), Storage::id)
}

val storage = Storage(listOf("item1", "item2"))
val json = Json.encodeToString(sound)
println(json)
```

Resulting JSON:

```json
[
	"item1",
	"item2"
]
```

**Explanation:**

- **Serializer Implementation**: The `InlineSerializer` takes a serializer for the property type and the property reference itself.
- **Usage**: By annotating the `Storage` class with `@Serializable` and specifying the custom serializer, only the `list` property is
  serialized directly as a JSON array.

---

## 5. Polymorphic Serialization with Namespaces

In some cases, you'll need to serialize polymorphic types with a namespace to ensure type safety and accurate representation of the
serialized data. You can achieve this by adding `@SerialName("namespace:type")` to all your classes, but it can be tedious and error-prone.
Instead, you can use a custom serializer to handle this efficiently.

### NamespacedPolymorphicSerializer

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

open class NamespacedPolymorphicSerializer<T : Any>(
	private val kClass: KClass<T>,
	private val outputName: String = "type",
	private val skipOutputName: Boolean = false,
	private val moveIntoProperty: String? = null,
	private val namespaceName: String = "minecraft"
) : KSerializer<T> {
	override val descriptor = serialDescriptor<JsonElement>()

	override fun deserialize(decoder: Decoder) = error("${kClass.simpleName} cannot be deserialized")

	override fun serialize(encoder: Encoder, value: T) {
		require(encoder is JsonEncoder) { "This serializer can only be used with Json" }
		require(kClass.isInstance(value) && value::class != kClass) { "Value must be an instance of ${kClass.simpleName}" }

		val valueClassName = value::class.simpleName!!.snakeCase()
		val outputClassName = when {
			value::class.annotations.any { it is SerialName } -> value::class.annotations.filterIsInstance<SerialName>().first().value
			else -> valueClassName
		}

		val serializer = encoder.serializersModule.getPolymorphic(kClass, value)
			?: encoder.serializersModule.getContextual(value::class)
			?: encoder.serializersModule.serializer(value::class.createType())

		val valueJson = encoder.json.encodeToJsonElement(serializer, value)
		if (runCatching { valueJson.jsonObject }.isFailure) {
			encoder.encodeJsonElement(valueJson)
			return
		}

		val finalJson = when (moveIntoProperty) {
			null -> buildJsonObject {
				if (!skipOutputName) put(outputName, "$namespaceName:$outputClassName")
				valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
			}

			else -> buildJsonObject {
				if (!skipOutputName) put(outputName, "$namespaceName:$outputClassName")

				when (valueJson) {
					is JsonObject -> putJsonObject(moveIntoProperty) {
						valueJson.jsonObject.filterKeys { it != outputName }.forEach(::put)
					}

					else -> put(moveIntoProperty, valueJson)
				}
			}
		}

		encoder.encodeJsonElement(finalJson)
	}
}
```

### Benefits

- **Type Safety**: Ensures that the serialized data accurately represents the object's type.
- **Readability**: Incorporates namespaces, making serialized data more understandable.
- **Flexibility**: Handles various serialization formats like JSON and NBT seamlessly.

### Implementation Tips

- **Consistent Naming**: Use consistent naming conventions across namespaces to avoid conflicts.
- **Serializer Registration**: Ensure all polymorphic types are registered within the serializers module.
- **Error Handling**: Implement robust error handling for unregistered or unsupported types.

### Example Usage

The parent must be a sealed class; a sealed interface won't work.

```kotlin
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json

@Serializable(with = Shape.ShapeSerializer::class)
sealed class Shape {
	data object ShapeSerializer : NamespacedPolymorphicSerializer<Shape>(Shape::class, namespaceName = "shapes")


	@Serializable
	data class Circle(val radius: Double) : Shape()

	@Serializable
	data class Rectangle(val width: Double, val height: Double) : Shape()
}

val circle = Shape.Circle(5.0)
val json = Json.encodeToString(circle)
println(json)
```

Resulting JSON:

```json
{
	"type": "shapes:circle",
	"radius": 5.0
}
```

You have many options to customize the serializer:

- `outputName`: The name of the key that will contain the namespace and the type.
- `skipOutputName`: If `true`, the `outputName` key won't be added to the JSON.
- `moveIntoProperty`: If not `null`, the JSON will be moved into a property with the given name.
- `namespaceName`: The namespace that will be added before the type.

**Example with `moveIntoProperty`:**

```kotlin
data object ShapeSerializer : NamespacedPolymorphicSerializer<Shape>(
	Shape::class,
	moveIntoProperty = "shape",
	namespaceName = "shapes"
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

---

## 6. Optimizing JSON Structures

Efficient JSON structures lead to reduced payload sizes and faster processing times. **kotlinx.serialization** provides serializers that
optimize JSON output based on specific criteria.

### SinglePropertySimplifierSerializer

Reduces JSON payloads by simplifying classes with a single non-null property.

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtEncoder

open class SinglePropertySimplifierSerializer<T : Any, P : Any>(
	private val kClass: KClass<T>,
	private val property: KProperty1<T, P>,
) : KSerializer<T> {
	override val descriptor = buildClassSerialDescriptor("${kClass.simpleName!!}SimplifiableSerializer") {
		element(property.name, serialDescriptor(property.returnType))
	}

	override fun serialize(encoder: Encoder, value: T) {
		require(kClass.isInstance(value) && value::class == kClass) { "Value must be instance of ${kClass.simpleName}" }
		var propertySerializer = encoder.serializersModule.serializer(property.returnType) as KSerializer<P>

		property.annotations.filterIsInstance<Serializable>().firstOrNull()?.let {
			propertySerializer = (it.with.objectInstance ?: it.with.createInstance()) as KSerializer<P>
		}

		val propertiesOrder = kClass.java.declaredFields.withIndex().associate { it.value.name to it.index }
		val properties = kClass.memberProperties.associateBy { it.name }.toSortedMap(compareBy { propertiesOrder[it] })
		val propertyValue = properties[property.name]
		val otherProperties = properties.filterKeys { it != property.name }
		if (otherProperties.all {
				it.value.getter.isAccessible = true
				it.value.getter.call(value) == null
			} && propertyValue != null
		) {
			propertyValue.getter.isAccessible = true
			encoder.encodeSerializableValue(propertySerializer, propertyValue.getter.call(value) as P)
		} else
		// default serializer is the current class, so we can't use it to encode the value, we have to create an object by hand
			when (encoder) {
				is JsonEncoder -> encoder.encodeJsonElement(buildJsonObject {
					properties.forEach { (name, property) ->
						property.getter.isAccessible = true
						val propertyValue = property.getter.call(value)
						if (propertyValue != null) {
							val serialName = property.annotations.filterIsInstance<SerialName>().firstOrNull()?.value ?: name
							put(
								serialName,
								if (name == this@SinglePropertySimplifierSerializer.property.name)
									encoder.json.encodeToJsonElement(propertySerializer, propertyValue as P)
								else
									encoder.json.encodeToJsonElement(
										(property.annotations.filterIsInstance<Serializable>().firstOrNull()?.let {
											(it.with.objectInstance ?: it.with.createInstance())
										} ?: encoder.serializersModule.serializer(property.returnType)) as KSerializer<Any>,
										propertyValue
									)
							)
						}
					}
				})

				is NbtEncoder -> encoder.encodeInline(descriptor).encodeSerializableValue(NbtCompound.serializer(), buildNbtCompound {
					properties.forEach { (name, property) ->
						property.getter.isAccessible = true
						val propertyValue = property.getter.call(value)
						if (propertyValue != null) {
							val serialName = property.annotations.filterIsInstance<SerialName>().firstOrNull()?.value ?: name
							put(
								serialName,
								if (name == this@SinglePropertySimplifierSerializer.property.name)
									encoder.nbt.encodeToNbtTag(propertySerializer, propertyValue as P)
								else
									encoder.nbt.encodeToNbtTag(
										(property.annotations.filterIsInstance<Serializable>().firstOrNull()?.let {
											(it.with.objectInstance ?: it.with.createInstance())
										} ?: encoder.serializersModule.serializer(property.returnType)) as KSerializer<Any>,
										propertyValue
									)
							)
						}
					}
				})

				else -> error("Unsupported encoder type: ${encoder::class.simpleName}")
			}
	}

	override fun deserialize(decoder: Decoder) =
		error("${kClass.simpleName} is not deserializable with ${this::class.simpleName}.")
}
```

**Example Usage:**

```kotlin
import kotlinx.serialization.Serializable

@Serializable(with = Config.ConfigSerializer::class)
data class Config(
	val enabled: Boolean,
	val retryCount: Int? = null
) {
	data object ConfigSerializer : SinglePropertySimplifierSerializer<Config, Boolean>(Config::class, Config::enabled)
}


val config1 = Config(enabled = true)
val json1 = Json.encodeToString(config1)
println(json1) // Outputs: true

val config2 = Config(enabled = true, retryCount = 3)
val json2 = Json.encodeToString(config2)
println(json2) // Outputs: {"enabled":true,"retryCount":3}
```

- If `retryCount` is `null`, the JSON will be:

  ```json
  true
  ```

- If `retryCount` is not `null`, the JSON will be:

  ```json
  {
	  "enabled": true,
	  "retryCount": 3
  }
  ```

---

## 6. Handling Complex Data Types

Complex data types like lists of multiple types or triples require specialized serializers to maintain data integrity during serialization.

### TripleAsArraySerializer

Serializes Kotlin `Triple` objects as JSON arrays for compact representation.

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection

typealias TripleAsArray<A, B, C> = @Serializable(TripleAsArraySerializer::class) Triple<A, B, C>

class TripleAsArraySerializer<A, B, C>(
	private val firstSerializer: KSerializer<A>,
	private val secondSerializer: KSerializer<B>,
	private val thirdSerializer: KSerializer<C>,
) : KSerializer<Triple<A, B, C>> {
	override val descriptor = mixedListSerialDescriptor(firstSerializer, secondSerializer, thirdSerializer)

	override fun deserialize(decoder: Decoder) = error("TripleAsArray is not meant to be deserialized")

	override fun serialize(encoder: Encoder, value: Triple<A, B, C>) =
		encoder.encodeCollection(descriptor, 3) {
			encodeSerializableElement(descriptor, 0, firstSerializer, value.first)
			encodeSerializableElement(descriptor, 1, secondSerializer, value.second)
			encodeSerializableElement(descriptor, 2, thirdSerializer, value.third)
		}
}
```

**Example Usage:**

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
	val position: TripleAsArray<Double, Double, Double>
)


val coordinates = Coordinates(Triple(1.0, 2.0, 3.0))
val json = Json.encodeToString(coordinates)
println(json)
```

Resulting JSON:

```json
[
	1.0,
	2.0,
	3.0
]
```

### InlinableListSerializer

Efficiently serializes lists that can be inlined based on their size, optimizing JSON structures.

```kotlin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias InlinableList<T> = @Serializable(with = InlinableListSerializer::class) List<T>

fun <T> inlinableListSerializer(kSerializer: KSerializer<T>): KSerializer<InlinableList<T>> = InlinableListSerializer(kSerializer)

@OptIn(ExperimentalSerializationApi::class)
open class InlinableListSerializer<T>(private val kSerializer: KSerializer<T>) : KSerializer<List<T>> {
	override val descriptor = ListSerializer(kSerializer).descriptor

	override fun deserialize(decoder: Decoder) = error("List cannot be deserialized")

	override fun serialize(encoder: Encoder, value: List<T>) = when (value.size) {
		1 -> encoder.encodeSerializableValue(kSerializer, value[0])
		else -> encoder.encodeSerializableValue(ListSerializer(kSerializer), value)
	}
}
```

**Usage Example:**

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class UserRoles(
	val roles: InlinableList<String> = emptyList()
)

val user = UserRoles(listOf("admin", "user"))
val json = Json.encodeToString(user)
println(json)

val user2 = UserRoles(listOf("admin"))
val json2 = Json.encodeToString(user2)
println(json2)
```

- If `roles` has one item:

  ```json
  "admin"
  ```

- If `roles` has multiple items:

  ```json
  ["admin", "user"]
  ```

---

## 7. Best Practices and Performance Tips

Ensuring that your serialization logic is both efficient and maintainable requires adherence to certain best practices and performance
optimization strategies.

### Serializer Reusability

- **Modular Design**: Design serializers to be reusable across different classes and modules.
- **Generic Serializers**: Utilize generic serializers to handle multiple data types with similar serialization logic.
- **Serializer Registration**: Centralize serializer registrations to avoid redundancy and simplify maintenance.

### Performance Optimization

- **Lazy Serialization**: Delay serialization of non-essential properties until necessary.
- **Batch Processing**: Serialize multiple objects in batches to reduce processing overhead.
- **Efficient Data Structures**: Use data structures that are optimized for serialization, such as immutable lists and maps.

### Error Handling

- **Graceful Degradation**: Implement fallback mechanisms for serialization failures.
- **Descriptive Errors**: Provide clear and descriptive error messages to facilitate debugging.
- **Validation**: Validate data before serialization to ensure consistency and integrity.

**Example:**

```kotlin
override fun deserialize(decoder: Decoder): T {
	try {
		val value = decoder.decodeString()
		return values.first { it.name.camelcase() == value }
	} catch (e: NoSuchElementException) {
		throw SerializationException("Unknown enum value: $value", e)
	}
}
```

---

## 8. Conclusion

Mastering **kotlinx.serialization** empowers you to handle complex serialization scenarios with ease and efficiency. By leveraging advanced
techniques such as custom serializers, polymorphic serialization, and optimized JSON structures, you can ensure that your Kotlin
applications remain performant, maintainable, and adaptable to evolving data requirements.

### Next Steps

- **Explore More**: Dive deeper into the [kotlinx.serialization documentation](https://kotlinlang.org/docs/serialization.html) to uncover
  additional features and capabilities.
- **Experiment**: Apply the discussed techniques in your projects to gain hands-on experience and refine your serialization strategies.
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

- **Custom Serializers**:
  [CamelcaseSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/CamelcaseSerializer.kt)
- **Enum Serializers**:
  [UppercaseSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/UppercaseSerializer.kt), [LowercaseSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/LowercaseSerializer.kt)
- **Polymorphic Serializer**:
  [NamespacedPolymorphicSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/NamespacedPolymorphicSerializer.kt)
- **Transforming Serializer**:
  [ProviderSerializer.kt](https://github.com/Ayfri/Kore/blob/master/kore/src/main/kotlin/io/github/ayfri/kore/serializers/ProviderSerializer.kt)

### Further Reading

- [kotlinx.serialization GitHub Repository](https://github.com/Kotlin/kotlinx.serialization)
- [Official Kotlin Serialization Documentation](https://kotlinlang.org/docs/serialization.html)
- [Advanced Serialization with kotlinx.serialization](https://kotlinlang.org/docs/serialization-custom.html)
