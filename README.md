# `damcli`

This is a cli tool that knows how to delete docker images from artifactory (to clean up space).

# Building
You can build the tool using gradle with:
`./gradlew cliJar`

this should produce a `damcli-0.1-cli.jar` jar in `build/libs/`

# Running

you can run this jar like any other using
`java -jar build/libs/damcli-0.1-cli.jar`.
It will provide some help to guide you on how to use it, like so:

```
Usage: damcli [-hV] [-a=<apiKey>] [-s=<server>] COMMAND
  -a, --api-key=<apiKey>   The artifactory api key to use
                           you can find instructions on how to get the key from:
                             https://www.jfrog.com/confluence/display/RTF/Updating+Your+Profile#UpdatingYourProfile-APIKey
                           will use the value from ARTIFACTORY_API_KEY if not provided
  -h, --help               Show this help message and exit.
  -s, --server=<server>    The base URI of the artifactory server to connect to, like:
                             https://my.artifactory.server/artifactory/
                           will try and pull the value from ARTIFACTORY_ENDPOINT if not provided
  -V, --version            Print version information and exit.
Commands:
  prune
  images
  repos
```

and then running a command like `prune` will also provide usage help:
```
Usage: damcli prune [-t] [-d=<arg3>] -i=<arg1> [-k=<arg2>] -r=<arg0>
  -d, --days=<arg3>         The minimum age of items to keep
                            Any tag older than this value will be deleted
  -i, --docker-image=<arg1>
  -k, --keep=<arg2>         Minimum number of tags to keep
  -r, --repository=<arg0>
  -t, --dry-run             Only print out the tags that would be deleted
                            this wont actually do any clean up or deletes
```
