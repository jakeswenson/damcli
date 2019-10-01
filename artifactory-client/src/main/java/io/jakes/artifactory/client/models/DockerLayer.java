package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import io.jakes.artifactory.client.ArtifactoryClient;

@AutoValue
@JsonDeserialize(builder = AutoValue_DockerLayer.Builder.class)
public abstract class DockerLayer {
  public abstract String blobSum();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("blobSum")
    Builder blobSum(final String blobSum);

    DockerLayer build();
  }
}
