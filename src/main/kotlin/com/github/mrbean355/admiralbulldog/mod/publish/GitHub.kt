package com.github.mrbean355.admiralbulldog.mod.publish

import com.github.mrbean355.admiralbulldog.mod.util.GitHubService
import com.github.mrbean355.admiralbulldog.mod.util.ReleaseRequest
import com.vdurmont.semver4j.Semver
import okhttp3.MediaType
import okhttp3.RequestBody
import org.slf4j.LoggerFactory
import java.io.File

object GitHub {
    private val logger = LoggerFactory.getLogger(GitHub::class.java)

    fun getNextModVersion(): Semver {
        val response = GitHubService.INSTANCE.getLatestRelease().execute()
        val body = response.body()

        if (!response.isSuccessful || body == null) {
            error("Failed to get latest release info: $response")
        }

        check(body.tagName.startsWith('v')) { "Unexpected tag format: ${body.tagName}" }
        return Semver(body.tagName.drop(1)).withIncPatch()
    }

    fun publishNewRelease(version: Semver, authToken: String) {
        logger.info("Creating release $version on GitHub...")
        val response = GitHubService.INSTANCE.createRelease("Bearer $authToken", createRequest(version, draft = true)).execute()

        val body = response.body()
        if (!response.isSuccessful || body == null) {
            error("Failed to create release: $response")
        }
        logger.info("Release '${body.id}' created, uploading assets...")
        uploadReleaseAssets(body.id, authToken)

        logger.info("Assets uploaded, publishing release...")
        val updateResponse = GitHubService.INSTANCE.updateRelease("Bearer $authToken", body.id, createRequest(version, draft = false)).execute()
        check(updateResponse.isSuccessful) { "Failed to update release: $updateResponse" }

        logger.info("Done!")
    }

    private fun createRequest(nextVersion: Semver, draft: Boolean): ReleaseRequest {
        return ReleaseRequest(
                tagName = "v$nextVersion",
                targetCommitish = "master",
                name = nextVersion.toString(),
                body = "Made the mod compatible with the latest Dota 2 update.",
                draft = draft,
                preRelease = false
        )
    }

    private fun uploadReleaseAssets(releaseId: Long, gitHubAuthToken: String) {
        // Upload VPK
        logger.info("Uploading VPK asset...")
        val vpk = File("pak01_dir.vpk")
        val vpkResponse = GitHubService.INSTANCE.uploadReleaseAsset(
                auth = "Bearer $gitHubAuthToken",
                releaseId = releaseId,
                body = RequestBody.create(MediaType.parse("application/octet"), vpk.readBytes()),
                name = vpk.name
        ).execute()

        check(vpkResponse.isSuccessful) { "VPK asset upload failed: $vpkResponse" }
        logger.info("VPK upload complete; $vpkResponse")

        // Upload checksum
        logger.info("Uploading checksum asset...")
        val checksum = File("pak01_dir.vpk.sha512")
        val checksumResponse = GitHubService.INSTANCE.uploadReleaseAsset(
                auth = "Bearer $gitHubAuthToken",
                releaseId = releaseId,
                body = RequestBody.create(MediaType.parse("application/octet"), checksum.readBytes()),
                name = checksum.name
        ).execute()

        check(checksumResponse.isSuccessful) { "Checksum asset upload failed: $checksumResponse" }
        logger.info("Checksum upload complete; $checksumResponse")
    }
}