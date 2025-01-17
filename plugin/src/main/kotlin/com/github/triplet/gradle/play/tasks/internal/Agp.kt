package com.github.triplet.gradle.play.tasks.internal

import com.android.build.api.artifact.ArtifactType
import com.android.build.api.artifact.BuildableArtifact
import com.android.build.gradle.internal.api.InstallableVariantImpl
import com.android.build.gradle.internal.scope.InternalArtifactType
import com.github.triplet.gradle.play.internal.orNull
import org.gradle.api.file.RegularFile
import java.io.File

fun PlayPublishTaskBase.findBundleFile(): File? {
    val customDir = extension._artifactDir

    return if (customDir == null) {
        val installable = variant as InstallableVariantImpl

        // TODO remove when AGP 3.6 is the minimum
        fun getFinalArtifactCompat(): Set<File> = try {
            installable.getFinalArtifact(InternalArtifactType.BUNDLE).files
        } catch (e: NoSuchMethodError) {
            (installable.javaClass.getMethod("getFinalArtifact", ArtifactType::class.java)
                    .invoke(installable, InternalArtifactType.BUNDLE) as BuildableArtifact).files
        }

        installable.variantData.scope.artifacts
                .getFinalProduct<RegularFile>(InternalArtifactType.BUNDLE)
                .get().asFile.orNull() ?: getFinalArtifactCompat().singleOrNull()
    } else {
        customDir.listFiles().orEmpty().singleOrNull { it.extension == "aab" }.also {
            if (it == null) logger.warn("Warning: no App Bundle found in '$customDir' yet.")
        }
    }
}
