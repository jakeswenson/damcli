package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import io.jakes.artifactory.client.ArtifactoryClient;

@AutoValue
@JsonDeserialize(builder = AutoValue_DockerManifest.Builder.class)
public abstract class DockerManifest {
  public abstract String name();

  public abstract String tag();

  public abstract ImmutableList<DockerLayer> fsLayers();

  public abstract ImmutableList<Object> history();

  public abstract String signature();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("name")
    Builder name(final String value);

    @JsonProperty("tag")
    Builder tag(final String value);

    @JsonProperty("fsLayers")
    Builder fsLayers(final ImmutableList<DockerLayer> value);

    @JsonProperty("history")
    Builder history(final ImmutableList<Object> history);

    @JsonProperty("signature")
    Builder signature(final String value);

    DockerManifest build();
  }
}
