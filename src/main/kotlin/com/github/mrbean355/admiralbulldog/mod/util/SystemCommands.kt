package com.github.mrbean355.admiralbulldog.mod.util

import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit.MINUTES

class ExecResult(
        val exitCode: Int,
        val output: String
)

private val logger = LoggerFactory.getLogger("SystemCommands")

/**
 * Run a command on the operating system.
 * Throws an exception if the exit code is not in [allowedExitCodes].
 * @return [ExecResult] containing the exit code and output.
 */
fun exec(command: String, allowedExitCodes: List<Int> = listOf(0)): ExecResult {
    logger.info("Exec: $command")

    val process = ProcessBuilder(command.split("\\s".toRegex()))
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start()

    val output = process.inputStream.bufferedReader().readText().trim()
    process.waitFor(15, MINUTES)
    if (output.isNotEmpty()) {
        logger.info(output)
    }
    check(process.exitValue() in allowedExitCodes) {
        "Unexpected exit code '${process.exitValue()}' for '$command'."
    }
    return ExecResult(process.exitValue(), output)
}