package com.github.mrbean355.admiralbulldog.mod.compile

import com.github.mrbean355.admiralbulldog.mod.util.STRINGS_FILES
import com.github.mrbean355.admiralbulldog.mod.util.GitHubService
import org.slf4j.LoggerFactory
import java.io.File

/** Which files to replace text in. */
private val TARGET_FILES = listOf("abilities_english.txt", "dota_english.txt")

/**
 * Downloads the latest applicable strings files and makes the replacements.
 */
object StringsFiles {
    private val logger = LoggerFactory.getLogger(StringsFiles::class.java)

    fun makeReplacements() {
        File(STRINGS_FILES).also {
            if (it.exists()) {
                it.deleteRecursively()
            }
            it.mkdirs()
        }
        TARGET_FILES.forEach {
            logger.info("Replacing: $it")
            downloadStringsFile(it)
            TextReplacements.applyToFile(File(STRINGS_FILES, it))
        }
    }

    private fun downloadStringsFile(fileName: String): File {
        val response = GitHubService.INSTANCE.getStringsFile(fileName).execute()
        val bytes = response.body()?.bytes() ?: error("Null body received for $fileName")
        return File(STRINGS_FILES, fileName).apply {
            writeBytes(bytes)
        }
    }
}
