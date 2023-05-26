package repository

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import repository.model.User

object JwtUtils {
  val secretKey = "mySecretKey"

  def generateJwt(user: User): String = {
    val claim = JwtClaim(
      content = s"""{"name": "${user.username}"}""",
      expiration = Some(System.currentTimeMillis() / 1000 + 3600) // 1 hour
    )
    Jwt.encode(claim, secretKey, JwtAlgorithm.HS256)
  }
}