package com.github.mrbean355.admiralbulldog.mod.compile

import com.github.mrbean355.admiralbulldog.mod.util.GitHubService
import com.github.mrbean355.admiralbulldog.mod.util.SCRIPT_FILES
import java.io.File

private val MS_PER_FRAME = mapOf(
        "cry.png" to 100,
        "eyeroll.png" to 100,
        "heart_kiss.png" to 25,
        "laugh.png" to 15,
        "rage.png" to 25
)
private val KEY_VALUE = Regex("^\\s+\"(.*)\"\\s+\"(.*)\"$")

object Emoticons {

    fun makeReplacements() {
        File(SCRIPT_FILES).also {
            if (it.exists()) {
                it.deleteRecursively()
            }
            it.mkdirs()
        }

        val emoticonsFile = downloadEmoticonsFile()
        val toReplace = MS_PER_FRAME.keys.toMutableSet()
        val output = StringBuilder()
        var imageName = ""

        emoticonsFile.forEachLine { line ->
            val result = KEY_VALUE.find(line)
            val key = result?.groups?.get(1)?.value
            val value = result?.groups?.get(2)?.value
            var newLine = line

            if (key != null && value != null) {
                when (key) {
                    "image_name" -> imageName = value
                    "ms_per_frame" -> {
                        if (imageName.isNotEmpty() && imageName in MS_PER_FRAME) {
                            newLine = "\t\t\"ms_per_frame\" \"${MS_PER_FRAME.getValue(imageName)}\""
                            toReplace -= imageName
                            imageName = ""
                        }
                    }
                }
            }
            output.appendln(newLine)
        }

        if (toReplace.isNotEmpty()) {
            error("Replacements not made: $toReplace")
        }

        emoticonsFile.writeText(output.toString())
    }

    private fun downloadEmoticonsFile(): File {
        val response = GitHubService.INSTANCE.getEmoticonsFile().execute()
        val bytes = response.body()?.bytes() ?: error("Null body received for emoticons.txt")
        return File(SCRIPT_FILES, "emoticons.txt").apply {
            writeBytes(bytes)
        }
    }
}