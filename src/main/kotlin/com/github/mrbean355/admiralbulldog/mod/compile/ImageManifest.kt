package com.github.mrbean355.admiralbulldog.mod.compile

import java.io.File

private const val SUPPORTED_FILE_TYPE = ".png"
private const val MANIFEST_FILE = "manifest.xml"
private const val COMPILED_MANIFEST_FILE = "manifest.vxml_c"
private val IMAGES_PATH = "panorama" + File.separator + "images" + File.separator

/**
 * Generate an XML manifest file which lists all PNG images.
 * The Dota 2 resource compiler uses this file to determine how to compile the images.
 */
object ImageManifest {
    private var manifestFile: File? = null

    fun generate(modSourceDir: File) {
        val imagesRoot = File(modSourceDir, IMAGES_PATH)
        manifestFile = File(imagesRoot, MANIFEST_FILE).also {
            if (it.exists()) {
                it.delete()
            }
        }

        val allFiles = mutableListOf<String>()
        imagesRoot.findAllFiles(allFiles)

        val xml = buildString {
            append("<root>\n")
            append("  <Panel>\n")
            allFiles.forEach {
                check(it.endsWith(SUPPORTED_FILE_TYPE)) { "Unsupported file type: $it" }
                append("    <Image src=\"file://{images}/$it\" />\n")
            }
            append("  </Panel>\n")
            append("</root>\n")
        }

        manifestFile?.writeText(xml)
    }

    fun cleanUp(stagingDir: File) {
        manifestFile?.delete()
        File(stagingDir, IMAGES_PATH + COMPILED_MANIFEST_FILE).delete()
    }

    private fun File.findAllFiles(items: MutableList<String>) {
        if (isDirectory) {
            listFiles()?.forEach { it.findAllFiles(items) }
        } else {
            items += absolutePath
                    .substringAfter(IMAGES_PATH)
                    .replace(File.separatorChar, '/')
        }
    }
}
