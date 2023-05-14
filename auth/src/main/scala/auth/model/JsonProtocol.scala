package model

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import zio.schema.{DeriveSchema, Schema}

object JsonProtocol {
  implicit val userDecoder: Decoder[User] = deriveDecoder
  implicit val userEncoder: Encoder[User] = deriveEncoder

  implicit val userSchema: Schema[User] = DeriveSchema.gen[User]
}
