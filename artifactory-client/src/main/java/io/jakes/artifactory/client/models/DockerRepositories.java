package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import io.jakes.artifactory.client.ArtifactoryClient;

@AutoValue
@JsonDeserialize(builder = AutoValue_DockerRepositories.Builder.class)
public abstract class DockerRepositories {
  public abstract ImmutableList<String> repositories();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("repositories")
    Builder repositories(final ImmutableList<String> repositories);

    DockerRepositories build();
  }
}
