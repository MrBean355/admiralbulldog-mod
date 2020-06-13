package com.github.mrbean355.admiralbulldog.mod

import com.github.mrbean355.admiralbulldog.mod.compile.ResourceCompiling
import com.github.mrbean355.admiralbulldog.mod.publish.Publishing
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs.Mode.COMPILE
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs.Mode.PUBLISH

fun main(args: Array<String>) {
    val programArgs = ProgramArgs.from(args)
    when (programArgs.getMode()) {
        COMPILE -> ResourceCompiling.compileResources(programArgs.getDotaRootDirectory(), programArgs.getModDirectory(), programArgs.getModDestinationDirectory())
        PUBLISH -> Publishing.run(programArgs.getEmailAddress(), programArgs.getAuthToken())
    }
}
