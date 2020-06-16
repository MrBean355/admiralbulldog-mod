package com.github.mrbean355.admiralbulldog.mod

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import com.github.mrbean355.admiralbulldog.mod.compile.ResourceCompiling
import com.github.mrbean355.admiralbulldog.mod.publish.Publishing
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs.Mode.COMPILE
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs.Mode.PUBLISH
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    setUp()
    val programArgs = ProgramArgs.from(args)
    when (programArgs.getMode()) {
        COMPILE -> ResourceCompiling.compileResources(programArgs.getDotaRootDirectory(), programArgs.getModDirectory(), programArgs.getModDestinationDirectory())
        PUBLISH -> Publishing.run(programArgs.getEmailAddress(), programArgs.getAuthToken())
    }
}

private fun setUp() {
    val logger = LoggerFactory.getLogger("org.eclipse.jgit.internal.storage.file.FileSnapshot") as? Logger
    logger?.level = Level.OFF
}
