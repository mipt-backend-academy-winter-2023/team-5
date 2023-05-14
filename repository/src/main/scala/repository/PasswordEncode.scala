package repository

import java.security.MessageDigest

object PasswordEncode {
  def sha256Hash(input: String): String = {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(input.getBytes("UTF-8"))

    val hexString = new StringBuilder
    for (i <- 0 until bytes.length) {
      val hex = Integer.toHexString(0xff & bytes(i))
      if (hex.length == 1)
        hexString.append('0')
      hexString.append(hex)
    }

    hexString.toString
  }

  def encode(password: String) : String = {
    sha256Hash(password)
  }
}
