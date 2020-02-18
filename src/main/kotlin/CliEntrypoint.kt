import cli.*
import com.google.common.base.Strings
import io.jakes.artifactory.client.ArtifactoryClient
import io.jakes.artifactory.client.Mappers
import picocli.CommandLine
import kotlin.system.exitProcess

@CommandLine.Command(
  name = "damcli",
  mixinStandardHelpOptions = true,
  usageHelpWidth = 125,
  synopsisSubcommandLabel = "COMMAND"
)
object AllCommands : Runnable {
  override fun run() {
    CommandLine.usage(this, System.out)
  }

  private fun handleResult(result: Result): Int {
    when (result) {
      is Result.Ok<*> -> {
        val json = Mappers.json().writerWithDefaultPrettyPrinter().writeValueAsString(result.result)
        println(json)
        return 0
      }
      is Result.Error -> {
        println(result.response)
        return 1
      }
    }
  }

  @CommandLine.Option(
    required = true,
    names = ["-a", "--api-key"],
    defaultValue = "\${env:ARTIFACTORY_API_KEY}",
    description = [
      "The artifactory api key to use",
      "you can find instructions on how to get the key from:",
      "  https://www.jfrog.com/confluence/display/RTF/Updating+Your+Profile#UpdatingYourProfile-APIKey",
      "will use the value from ARTIFACTORY_API_KEY if not provided"
    ]
  )
  var apiKey: String = ""

  @CommandLine.Option(
    required = true,
    names = ["-s", "--server"],
    defaultValue = "\${env:ARTIFACTORY_ENDPOINT}",
    description = [
      "The base URI of the artifactory server to connect to, like:",
      "  https://my.artifactory.server/artifactory/",
      "will try and pull the value from ARTIFACTORY_ENDPOINT if not provided",
      "currently ARTIFACTORY_ENDPOINT='\${DEFAULT-VALUE}'"
    ]
  )
  var server: String = ""

  @CommandLine.Command(name = "repos")
  fun listRepositories(): Int {
    val client = ClientFactory.createClient(ArtifactorySettings(server, apiKey))
    return handleResult(listRepos(client))
  }

  @CommandLine.Command(name = "images")
  fun listDockerImages(
    @CommandLine.Option(names = ["-r", "--repository"], required = true)
    repoName: String
  ): Int {
    val client = ClientFactory.createClient(ArtifactorySettings(server, apiKey))
    return handleResult(listImages(client, ListImagesArgs(repoName)))
  }

  @CommandLine.Command(name = "prune")
  fun cleanDockerTags(
    @CommandLine.Option(names = ["-r", "--repository"], required = true)
    repoName: String,
    @CommandLine.Option(names = ["-i", "--docker-image"])
    dockerImage: String?,
    @CommandLine.Option(names = ["--all-images"])
    allImages: Boolean = false,
    @CommandLine.Option(
      names = ["-k", "--keep"], defaultValue = "1",
      description = ["Minimum number of tags to keep"]
    )
    keep: Int = 1,
    @CommandLine.Option(
      names = ["-d", "--days"], defaultValue = "0", description = [
        "The minimum age of items to keep",
        "Any tag older than this value will be deleted"
      ]
    )
    olderThanDays: Long = 0,
    @CommandLine.Option(
      names = ["-t", "--dry-run"], description = [
        "Only print out the tags that would be deleted",
        "this wont actually do any clean up or deletes"]
    )
    isTestRun: Boolean = false
  ): Int {
    val client = ClientFactory.createClient(ArtifactorySettings(server, apiKey))
    if (Strings.isNullOrEmpty(dockerImage) && !allImages) {
      return handleResult(Result.Error("Must pass either image name `-i`/`--docker-image` or `--all-images`"))
    }
    val opts = CleanOptions(
      repoName,
      image = CleanImageArgs(allImages, dockerImage),
      keep = keep,
      olderThanDays = olderThanDays,
      isTestRun = isTestRun
    )
    return handleResult(cleanDockerImageTags(opts, client))
  }
}

fun main(args: Array<String>) {
  val commandLine = CommandLine(AllCommands)
  exitProcess(commandLine.execute(*args))
}

object ClientFactory : ArtifactoryClientFactory {
  override fun createClient(artifactory: ArtifactorySettings): ArtifactoryClient {
    return ArtifactoryClient.createClient(artifactory.serverEndpoint, artifactory.apiKey)
  }
}

data class ArtifactorySettings(val serverEndpoint: String, val apiKey: String)

interface ArtifactoryClientFactory {
  fun createClient(artifactory: ArtifactorySettings): ArtifactoryClient
}
