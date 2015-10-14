package helpers

import java.security.MessageDigest


object Hash {

    def md5(str: String): String = {
        MessageDigest.getInstance("MD5").digest(str.getBytes()).map(0xFF & _).map {
            "%02x".format(_)
        }.foldLeft("") {
            _ + _
        }
    }

}
