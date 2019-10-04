package cli

import com.google.common.base.CharMatcher
import com.google.common.base.Splitter
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import io.jakes.artifactory.client.ArtifactoryClient
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

data class ListImagesArgs(val repoName: String)

fun listImages(client: ArtifactoryClient, listImagesArgs: ListImagesArgs): Result {
  val response = client.listDockerRepos(listImagesArgs.repoName).execute()

  if (!response.isSuccessful) {
    return Result.Error(response.errorBody()?.string())
  }

  return Result.Ok(response.body()?.repositories())
}

fun listRepos(client: ArtifactoryClient): Result {
  val response = client.listRepositories().execute()
  if (!response.isSuccessful) {
    return Result.Error(response.errorBody()?.string())
  }

  return Result.Ok(response.body()?.map { it.key() })
}

data class CleanOptions(
  val repoName: String,
  val imageName: String,
  val keep: Int,
  val olderThanDays: Long,
  val isTestRun: Boolean,
  val exclude: ImmutableSet<String> = ImmutableSet.of(),
  val excludeContains: ImmutableSet<String> = ImmutableSet.of()
)

fun cleanDockerImageTags(
  cleanOptions: CleanOptions,
  artifactoryClient: ArtifactoryClient
): Result {

  val allTags = artifactoryClient.listDockerTags(cleanOptions.repoName, cleanOptions.imageName).execute()

  if (!allTags.isSuccessful) {
    return Result.Error(allTags.errorBody()?.string())
  }

  val exclusionSet = setOf("latest") + cleanOptions.exclude

  val matcher = CharMatcher.`is`('/')
  val splitter = Splitter.on(matcher)

  data class TagInfo(val tag: String, val lastModified: OffsetDateTime) {}

  val files = artifactoryClient.listFiles(cleanOptions.repoName, cleanOptions.imageName).execute()
  val manifestFileName = "manifest.json"

  val manifests = files.body()!!.files()
    .filter { it.uri().endsWith(manifestFileName) }
    .map { TagInfo(splitter.split(matcher.trimLeadingFrom(it.uri())).first(), it.lastModified().toOffsetDateTime()) }

  var filtered = manifests.filter { it.tag !in exclusionSet }

  for (ec in cleanOptions.excludeContains)
    filtered = filtered.filter { !it.tag.contains(ec) }

  val olderThan = LocalDateTime.now().minusDays(cleanOptions.olderThanDays).atOffset(ZoneOffset.UTC)

  val tags =
    filtered
      .sortedBy { it.lastModified } // ascending by last modified date time
      .dropLast(cleanOptions.keep) // last because its ascending
      .takeWhile { olderThan.isAfter(it.lastModified) }
      .map { it.tag }

  if (!cleanOptions.isTestRun) {
    tags.forEach { tag ->
      artifactoryClient.deleteDockerByDigest(cleanOptions.repoName, cleanOptions.imageName, tag).execute()
    }
  }

  val listFilesRequest = artifactoryClient.listFilesWithFolders(cleanOptions.repoName, cleanOptions.imageName)

  val filesAndFoldersResponse = listFilesRequest.execute()

  val filesAndFolders = filesAndFoldersResponse.body()?.files() ?: ImmutableList.of()

  val folders = filesAndFolders.filter { it.folder() }

  val filesInFolders = folders.map { folder ->
    folder.uri().substringAfter('/') to filesAndFolders.filter { it.folder().not() && it.uri().startsWith(folder.uri()) }
      .map { it.uri().substringAfterLast('/') }
      .toSet()
  }.toMap()

  filesInFolders.forEach { (folder, folderFiles) ->
    if (!folderFiles.contains(manifestFileName)) {
      println("$folder does not have a docker manifest...")

      if (!cleanOptions.isTestRun) {
        val request = artifactoryClient.deleteTagFolder(cleanOptions.repoName, cleanOptions.imageName, folder)
        val deleteFolderResponse = request.execute()
        if (!deleteFolderResponse.isSuccessful) {
          println("Error(${deleteFolderResponse.code()}: ${deleteFolderResponse.errorBody()?.string()}")
        }
      }
    }
  }

  return Result.Ok(tags)
}
