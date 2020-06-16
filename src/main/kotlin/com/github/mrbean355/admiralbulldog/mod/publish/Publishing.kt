package com.github.mrbean355.admiralbulldog.mod.publish

import com.github.mrbean355.admiralbulldog.mod.compile.Emoticons
import com.github.mrbean355.admiralbulldog.mod.compile.StringsFiles
import com.github.mrbean355.admiralbulldog.mod.compile.Vpk
import com.github.mrbean355.admiralbulldog.mod.util.exec
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.slf4j.LoggerFactory

/**
 * Automated publishing of new mod releases.
 * Downloads the latest strings files and makes replacements. Publishes a new release if there are changed strings.
 * Run periodically by the build server.
 */
object Publishing {
    private val logger = LoggerFactory.getLogger(Publishing::class.java)

    fun run(emailAddress: String, authToken: String) {
        val repo = FileRepositoryBuilder()
                .findGitDir()
                .build()

        StringsFiles.makeReplacements()
        Emoticons.makeReplacements()

        if (repo.branch != "master") {
            logger.warn("On branch '${repo.branch}' instead of master; stopping")
            return
        }

        val git = Git(repo)
        val status = git.status().addPath("compiled").call()
        if (status.isClean) {
            logger.info("No changes detected; done")
            return
        }

        logger.info("Changes detected:")
        status.uncommittedChanges.forEach {
            logger.info("  - $it")
        }

        // Commit changes
        git.reset().call()
        git.add().addFilepattern("compiled").call()
        repo.config.apply {
            setString("user", null, "name", "Mike Johnston")
            setString("user", null, "email", "mrbean355@gmail.com")
            save()
        }
        try {
            git.commit().setMessage("Pull in the latest Dota 2 strings").call()

            val nextVersion = GitHub.getNextModVersion()
            git.tag().setName("v$nextVersion").setMessage("Auto-release $nextVersion").call()

            exec("git checkout develop")

            git.merge().include(repo.resolve("master")).setMessage("Merge branch 'master' into develop").call()

            exec("git push")
            exec("git checkout master")
            exec("git push")
            exec("git push --tags")

            Vpk.compile()

            GitHub.publishNewRelease(nextVersion, authToken)
        } finally {
            repo.config.clear()
        }
    }
}
