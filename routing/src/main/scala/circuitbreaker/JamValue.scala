package circuitbreaker

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class JamValue(jam_value: Int)

object JamValue {
  implicit val decoder: JsonDecoder[JamValue] =
    DeriveJsonDecoder.gen[JamValue]
  implicit val encoder: JsonEncoder[JamValue] =
    DeriveJsonEncoder.gen[JamValue]
  implicit val jamIdDecoder: Decoder[JamValue] = deriveDecoder
  implicit val jamIdEncoder: Encoder[JamValue] = deriveEncoder
}
