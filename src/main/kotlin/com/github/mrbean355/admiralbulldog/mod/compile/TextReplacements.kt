package com.github.mrbean355.admiralbulldog.mod.compile

import com.github.mrbean355.admiralbulldog.mod.util.TEXT_REPLACEMENTS
import java.io.File

/**
 * Only replace lines that match this pattern.
 *
 * Example:
 * 		"DOTA_Tooltip_Ability_item_hand_of_midas"			"Hand of Midas"    // optional comment which says stuff
 */
private val FILE_ENTRY_PATTERN = Regex("^\\s*\"(.*)\"\\s*\"(.*)\".*$")

// Symbols used in the mappings file:
private const val SEPARATOR = '='
private const val EXACT_REPLACE = '!'
private const val SINGLE_REPLACE = '@'
private const val COMMENT = '#'

object TextReplacements {
    private val containsReplacements = mutableMapOf<String, String>()
    private val exactReplacements = mutableMapOf<String, String>()
    private val singleReplacements = mutableMapOf<String, String>()

    init {
        loadReplacements()
    }

    fun applyToFile(input: File) {
        require(input.exists()) { "Input file doesn't exist: ${input.absolutePath}" }
        val output = StringBuilder()
        input.forEachLine { line ->
            val match = FILE_ENTRY_PATTERN.matchEntire(line)
            if (match == null) {
                output.append(line + "\n")
            } else {
                val key = match.groupValues[1]
                val oldValue = match.groupValues[2]
                var newValue = oldValue
                when {
                    key in singleReplacements -> newValue = singleReplacements[key]!!
                    oldValue in exactReplacements -> newValue = exactReplacements[oldValue]!!
                    else -> {
                        containsReplacements.forEach { (k, v) ->
                            newValue = newValue.replace(k, v)
                        }
                    }
                }
                output.append(line.replace("\"$oldValue\"", "\"$newValue\"") + "\n")
            }
        }
        input.writeText(output.toString())
    }

    private fun loadReplacements() {
        val file = File(TEXT_REPLACEMENTS)
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
                    when {
                        key.startsWith(EXACT_REPLACE) -> exactReplacements += key.drop(1) to value
                        key.startsWith(SINGLE_REPLACE) -> singleReplacements += key.drop(1) to value
                        else -> containsReplacements += key to value
                    }
                }
    }
}
