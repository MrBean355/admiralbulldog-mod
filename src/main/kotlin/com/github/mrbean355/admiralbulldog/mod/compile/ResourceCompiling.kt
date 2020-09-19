package com.github.mrbean355.admiralbulldog.mod.compile

import com.github.mrbean355.admiralbulldog.mod.util.COMPILED_FILES
import com.github.mrbean355.admiralbulldog.mod.util.COMPILER_OUTPUT
import com.github.mrbean355.admiralbulldog.mod.util.RESOURCE_COMPILER
import com.github.mrbean355.admiralbulldog.mod.util.exec
import org.slf4j.LoggerFactory
import java.io.File

private val RESOURCES_TO_COMPILE: Set<String> = setOf("sounds", "panorama", "materials")
private val FILE_RENAMES: Map<String, String> = mapOf(
        "panorama/images/hud/reborn/statbranch_button_bg_png.vtex_c" to "panorama/images/hud/reborn/statbranch_button_bg_psd.vtex_c",
        "panorama/images/textures/startup_background_logo_png.vtex_c" to "panorama/images/textures/startup_background_logo_psd.vtex_c",
        "panorama/images/pings/ping_world_png.vtex_c" to "panorama/images/pings/ping_world_psd.vtex_c"
)

object ResourceCompiling {
    private val logger = LoggerFactory.getLogger(ResourceCompiling::class.java)

    fun compileResources(dotaRoot: File, modSource: File, modDestination: File) {
        val dotaPath = dotaRoot.absolutePath
        val modPath = modSource.absolutePath
        val resourceCompiler = RESOURCE_COMPILER.format(dotaPath)
        val compilerOutput = COMPILER_OUTPUT.format(dotaPath, modSource.name)

        // 0: Create fresh output directory
        val outputDir = File(COMPILED_FILES)
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()

        // 1: Make text replacements
        StringsFiles.makeReplacements()
        Emoticons.makeReplacements()

        // 2: Prepare images
        ImageManifest.generate(File(modPath))

        // 3: Compile resources
        RESOURCES_TO_COMPILE.forEach {
            logger.info("Compiling $it...")
            exec("$resourceCompiler -r -i \"$modPath/$it/*\"")
            File(compilerOutput, it).copyRecursively(File(outputDir, it))
        }

        // 4: Rename special files
        FILE_RENAMES.forEach { (k, v) ->
            logger.info("Renaming $k...")
            File(outputDir, k).renameTo(File(outputDir, v))
        }

        // 5: Clean up
        logger.info("Cleaning up...")
        ImageManifest.cleanUp(outputDir)
        File(compilerOutput).deleteRecursively()

        // 6: Install locally
        Vpk.compileAndInstall(modDestination)
    }
}