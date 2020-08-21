package homework.infrastructure

import homework.api.dto.field.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.math.BigDecimal
import java.time.LocalDate

val json = Json {
    isLenient = true
    encodeDefaults = false
    prettyPrint = true
    serializersModule = SerializersModule {
        contextual(LocalDate::class, LocalDateSerializer)
        contextual(BigDecimal::class, BigDecimalSerializer)
        polymorphic(Field::class, CalculatedField::class, CalculatedField.serializer())
        polymorphic(Field::class, AggregateField::class, AggregateField.serializer())
        polymorphic(Field::class, LongField::class, LongField.serializer())
        polymorphic(Field::class, DateField::class, DateField.serializer())
        polymorphic(Field::class, DecimalField::class, DecimalField.serializer())
        polymorphic(Field::class, StringField::class, StringField.serializer())
    }
}


object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString())
    }

}

object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Decimal", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): BigDecimal {
        return BigDecimal(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(value.toString())
    }
}
