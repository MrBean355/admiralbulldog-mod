package com.github.mrbean355.admiralbulldog.mod.compile

import com.github.mrbean355.admiralbulldog.mod.util.CHECKSUM_FILE
import com.github.mrbean355.admiralbulldog.mod.util.COMPILED_FILES
import com.github.mrbean355.admiralbulldog.mod.util.VPK_FILE
import com.github.mrbean355.admiralbulldog.mod.util.exec
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Compile the mod's VPK file.
 */
object Vpk {

    fun compile(): File {
        exec("vpk -c $COMPILED_FILES $VPK_FILE")
        return File(VPK_FILE).apply {
            File(CHECKSUM_FILE).writeText(checksum())
        }
    }

    fun compileAndInstall(destination: File) {
        val vpk = compile()
        File(destination, vpk.name).also {
            if (it.exists()) {
                it.delete()
            }
            vpk.copyTo(it)
        }
    }

    private fun File.checksum(): String {
        val messageDigest = MessageDigest.getInstance("SHA-512")
        val result = messageDigest.digest(readBytes())
        val convertedResult = BigInteger(1, result)
        var hashText = convertedResult.toString(16)
        while (hashText.length < 32) {
            hashText = "0$hashText"
        }
        return hashText
    }
}