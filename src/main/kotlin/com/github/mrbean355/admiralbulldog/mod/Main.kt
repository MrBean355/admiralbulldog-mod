package com.github.mrbean355.admiralbulldog.mod

import com.github.mrbean355.admiralbulldog.mod.compile.Vpk
import com.github.mrbean355.admiralbulldog.mod.util.COMPILED_FILES
import com.github.mrbean355.admiralbulldog.mod.util.ProgramArgs
import java.io.File

fun main(args: Array<String>) {
    val programArgs = ProgramArgs.from(args)
    require(programArgs.getMode() == ProgramArgs.Mode.COMPILE)

    File(COMPILED_FILES).apply {
        deleteRecursively()
        mkdirs()
    }

    Vpk.compileAndInstall(programArgs.getModDestinationDirectory())
}