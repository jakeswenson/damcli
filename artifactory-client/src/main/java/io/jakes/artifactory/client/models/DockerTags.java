package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import io.jakes.artifactory.client.ArtifactoryClient;

@AutoValue
@JsonDeserialize(builder = AutoValue_DockerTags.Builder.class)
public abstract class DockerTags {
  public abstract String name();

  public abstract ImmutableList<String> tags();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("name")
    Builder name(final String name);

    @JsonProperty("tags")
    Builder tags(final ImmutableList<String> tags);

    DockerTags build();
  }
}
