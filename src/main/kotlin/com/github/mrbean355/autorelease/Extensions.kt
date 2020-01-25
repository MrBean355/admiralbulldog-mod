package com.github.mrbean355.autorelease

import java.io.File
import java.lang.ProcessBuilder.Redirect.PIPE
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

private val logFile by lazy { File("log.txt") }

fun runCommand(command: String, allowedExitCodes: List<Int> = listOf(0)): Pair<Int, String> {
    val process = ProcessBuilder(command.split("\\s".toRegex()))
            .redirectOutput(PIPE)
            .redirectError(PIPE)
            .start()

    process.waitFor(1, TimeUnit.HOURS)
    check(process.exitValue() in allowedExitCodes) {
        "Unexpected exit code '${process.exitValue()}' for '$command'."
    }
    val output = process.inputStream.bufferedReader().readText().trim()
    logFile.appendText("[${Date()}] $output\n")
    return process.exitValue() to output
}

fun File.checksum(): String {
    val messageDigest = MessageDigest.getInstance("SHA-512")
    val result = messageDigest.digest(readBytes())
    val convertedResult = BigInteger(1, result)
    var hashText = convertedResult.toString(16)
    while (hashText.length < 32) {
        hashText = "0$hashText"
    }
    return hashText
}
