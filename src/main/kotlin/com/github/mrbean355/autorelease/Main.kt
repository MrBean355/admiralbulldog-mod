package com.github.mrbean355.autorelease

import com.vdurmont.semver4j.Semver
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import kotlin.system.exitProcess

private const val DOTA_ROOT = "C:\\Steam Library\\steamapps\\common\\dota 2 beta"
private const val VPK_PATH = "$DOTA_ROOT\\game\\dota\\pak01_dir.vpk"
private const val RESOURCE_COMPILER = "$DOTA_ROOT\\game\\bin\\win64\\resourcecompiler.exe"
private const val COMPILER_OUTPUT = "$DOTA_ROOT\\game\\dota_addons\\admiralbulldog"

private var GITHUB_AUTH_TOKEN = ""

fun File.moveTo(path: String) {
    val target = File(path)
    if (target.exists()) {
        target.delete()
    }
    renameTo(target)
}

fun main(args: Array<String>) {
    GITHUB_AUTH_TOKEN = args.single()

    // Extract files from VPK
    println("Extracting VPK files...")
    runCommand("vpk \"$VPK_PATH\" -f resource/localization/abilities_english.txt -x .")
    runCommand("vpk \"$VPK_PATH\" -f resource/localization/dota_english.txt -x .")

    // Make string replacements
    println("Replacing strings...")
    Replacements.applyToFile("resource/localization/abilities_english.txt")
    Replacements.applyToFile("resource/localization/dota_english.txt")

    // Compile resources
    println("Compiling VPK...")
    runCommand("$RESOURCE_COMPILER -r -i sounds\\*")
    runCommand("$RESOURCE_COMPILER -r -i panorama\\*")

    val stagingDir = File("compile\\pak01_dir")
    stagingDir.mkdirs()

    // Copy to staging directory
    File("$COMPILER_OUTPUT\\sounds").copyRecursively(File(stagingDir, "sounds"))
    File("$COMPILER_OUTPUT\\panorama").copyRecursively(File(stagingDir, "panorama"))
    File("resource").copyRecursively(File(stagingDir, "resource"))

    // Rename some special cases
    File("compile\\pak01_dir\\panorama\\images\\hud\\reborn\\statbranch_button_bg_png.vtex_c")
            .moveTo("compile\\pak01_dir\\panorama\\images\\hud\\reborn\\statbranch_button_bg_psd.vtex_c")

    // Compile the VPK
    runCommand("compile\\vpk compile\\pak01_dir")
    File("compile\\pak01_dir.vpk").moveTo("pak01_dir.vpk")

    // Clean up
    stagingDir.deleteRecursively()
    File(COMPILER_OUTPUT).deleteRecursively()

    // VCS
    print("Getting latest release info... ")
    val latestReleaseResponse = service.getLatestRelease().execute()
    val latestReleaseBody = latestReleaseResponse.body()
    if (!latestReleaseResponse.isSuccessful || latestReleaseBody == null) {
        println("error: $latestReleaseBody")
        exitProcess(-1)
    }

    require(latestReleaseBody.tagName.startsWith('v')) { "Unexpected tag format: ${latestReleaseBody.tagName}" }
    val latestVersion = Semver(latestReleaseBody.tagName.drop(1))
    println(latestVersion)
    val nextVersion = latestVersion.withIncPatch()

    commitAndPush(nextVersion)
    publishReleaseToGitHub(nextVersion)

    println("Done!")
}

private fun commitAndPush(nextVersion: Semver) {
    println("Committing, tagging & pushing...")
    val previousCommit = runCommand("git rev-parse HEAD").second
    runCommand("git checkout master")
    runCommand("git pull")
    runCommand("git reset")
    runCommand("git add resource/localization/*")
    runCommand("git commit -m \"Merge in latest strings\"", allowedExitCodes = listOf(0, 1))

    val currentCommit = runCommand("git rev-parse HEAD").second
    if (previousCommit == currentCommit) {
        println("No changes committed; done!")
        exitProcess(status = 0)
    }

    runCommand("git push")

    runCommand("git tag -a v$nextVersion -m \"$nextVersion\"")
    runCommand("git checkout develop")
    runCommand("git pull")
    runCommand("git merge master")

    runCommand("git push")
    runCommand("git push --tags")
}

private fun publishReleaseToGitHub(nextVersion: Semver) {
    println("Creating release $nextVersion on GitHub...")
    val response = service.createRelease("Bearer $GITHUB_AUTH_TOKEN", CreateReleaseRequest(
            tagName = "v$nextVersion",
            targetCommitish = "master",
            name = nextVersion.toString(),
            body = "- Nothing exciting; applying the latest Dota 2 strings.",
            draft = true, // TODO: change to false when the process works.
            preRelease = false
    )).execute()

    val body = response.body()
    if (!response.isSuccessful || body == null) {
        println("Failed to create release: $response")
        exitProcess(-1)
    }
    println("Release '${body.id}' created, uploading assets...")
    uploadReleaseAssets(body.id)
}

private fun uploadReleaseAssets(releaseId: Long) {
    // Upload VPK
    println("Uploading VPK asset...")
    val vpk = File("pak01_dir.vpk")
    val vpkResponse = service.uploadReleaseAsset(
            auth = "Bearer $GITHUB_AUTH_TOKEN",
            releaseId = releaseId,
            body = RequestBody.create(MediaType.parse("application/octet"), vpk.readBytes()),
            name = vpk.name
    ).execute()
    println("VPK upload complete; $vpkResponse")

    // Upload checksum
    println("Uploading checksum asset...")
    val checksum = File("pak01_dir.vpk.sha512")
    checksum.writeText(vpk.checksum())
    val checksumResponse = service.uploadReleaseAsset(
            auth = "Bearer $GITHUB_AUTH_TOKEN",
            releaseId = releaseId,
            body = RequestBody.create(MediaType.parse("application/octet"), checksum.readBytes()),
            name = checksum.name
    ).execute()
    println("Checksum upload complete; $checksumResponse")
}
