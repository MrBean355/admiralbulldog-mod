package com.github.mrbean355.admiralbulldog.mod.util

import java.io.File

class ProgramArgs private constructor() {
    private var mode: Mode? = null
    private var dotaRoot: String? = null
    private var modName: String? = null
    private var emailAddress: String? = null
    private var authToken: String? = null

    fun getMode(): Mode = mode!!

    fun getDotaRootDirectory(): File = File(dotaRoot!!)

    fun getModDirectory(): File {
        val addons = File(getDotaRootDirectory(), "content/dota_addons")
        return File(addons, modName!!)
    }

    fun getModDestinationDirectory(): File {
        val addons = File(getDotaRootDirectory(), "game")
        return File(addons, modName!!)
    }

    fun getEmailAddress(): String = emailAddress!!

    fun getAuthToken(): String = authToken!!

    private fun validate() {
        require(mode != null) { "Missing 'compile' or 'publish' argument" }
        if (mode == Mode.COMPILE) {
            require(dotaRoot != null) { "Missing argument: $ARG_DOTA_ROOT" }
            require(modName != null) { "Missing argument: $ARG_MOD_NAME" }
        }
        if (mode == Mode.PUBLISH) {
            require(emailAddress != null) { "Missing environment variable: $ENV_EMAIL_ADDRESS" }
            require(authToken != null) { "Missing environment variable: $ENV_AUTH_TOKEN" }
        }
    }

    enum class Mode {
        COMPILE,
        PUBLISH
    }

    companion object {
        private const val ARG_COMPILE = "compile"
        private const val ARG_PUBLISH = "publish"

        private const val ARG_DOTA_ROOT = "--dota-root"
        private const val ARG_MOD_NAME = "--mod-name"
        private const val ENV_AUTH_TOKEN = "AUTH_TOKEN"
        private const val ENV_EMAIL_ADDRESS = "EMAIL_ADDRESS"

        fun from(args: Array<String>): ProgramArgs {
            val mutableArgs = args.toMutableList()
            val result = ProgramArgs()
            while (mutableArgs.isNotEmpty()) {
                when (val arg = mutableArgs.removeAt(0)) {
                    ARG_COMPILE -> result.mode = Mode.COMPILE
                    ARG_PUBLISH -> result.mode = Mode.PUBLISH
                    ARG_DOTA_ROOT -> result.dotaRoot = mutableArgs.removeAt(0)
                    ARG_MOD_NAME -> result.modName = mutableArgs.removeAt(0)
                    else -> throw IllegalArgumentException("Unexpected arg: $arg")
                }
            }
            result.emailAddress = System.getenv(ENV_EMAIL_ADDRESS)
            result.authToken = System.getenv(ENV_AUTH_TOKEN)
            result.validate()
            return result
        }
    }
}