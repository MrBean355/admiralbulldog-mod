package com.github.mrbean355.autorelease

import java.io.File

private const val MAPPINGS_FILE = "replacements.txt"

/**
 * Only replace lines that match this pattern.
 *
 * Example:
 * 		"DOTA_Tooltip_Ability_item_hand_of_midas"			"Hand of Midas"    // optional comment which says stuff
 */
private val FILE_ENTRY_PATTERN = Regex("^\\s*\"(.*)\"\\s*\"(.*)\".*$")

/** Charset used by the Dota string files. */
private val CHARSET = Charsets.UTF_16LE

// Symbols used in the replacements.txt file:
private const val SEPARATOR = '='
private const val EXACT_REPLACE = '!'
private const val COMMENT = '#'

object Replacements {
    private val containsReplacements = mutableMapOf<String, String>()
    private val exactReplacements = mutableMapOf<String, String>()

    init {
        loadReplacements()
    }

    fun applyToFile(filePath: String) {
        val input = File(filePath)
        require(input.exists()) { "Input file doesn't exist: ${input.absolutePath}" }
        val output = StringBuilder()
        input.forEachLine(CHARSET) { line ->
            val match = FILE_ENTRY_PATTERN.matchEntire(line)
            if (match == null) {
                output.append(line + "\n")
            } else {
                val oldValue = match.groupValues[2]
                var newValue = oldValue
                val exactReplacement = exactReplacements[oldValue]
                if (exactReplacement != null) {
                    newValue = exactReplacement
                } else {
                    containsReplacements.forEach { (k, v) ->
                        newValue = newValue.replace(k, v, ignoreCase = true)
                    }
                }
                output.append(line.replace("\"$oldValue\"", "\"$newValue\"") + "\n")
            }
        }
        input.writeText(output.toString(), CHARSET)
    }

    private fun loadReplacements() {
        val file = File(MAPPINGS_FILE)
        require(file.exists()) { "Replacements file doesn't exist: ${file.absolutePath}" }
        file.readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .filter { !it.startsWith(COMMENT) }
                .forEach { line ->
                    val parts = line
                            .substringBefore(COMMENT)
                            .split(SEPARATOR)

                    require(parts.size == 2) { "Invalid syntax: $line" }
                    val key = parts.first().trim()
                    val value = parts[1].trim()
                    if (key.startsWith(EXACT_REPLACE)) {
                        exactReplacements += key.drop(1) to value
                    } else {
                        containsReplacements += key to value
                    }
                }
    }
}
