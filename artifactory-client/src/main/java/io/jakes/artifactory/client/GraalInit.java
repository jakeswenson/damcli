package io.jakes.artifactory.client;

@SuppressWarnings("unused")
class GraalInit {
  private GraalInit() {
  }

  private static final ArtifactoryClient client_init;
  static {
    client_init = ArtifactoryClient.createClient("https://graal.init.jakes.io", "jakes_io_graal_init");
  }
}
