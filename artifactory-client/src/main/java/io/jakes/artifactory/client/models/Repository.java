package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import io.jakes.artifactory.client.ArtifactoryClient;

import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_Repository.Builder.class)
public abstract class Repository {
  public abstract String key();

  public abstract String type();

  public abstract Optional<String> description();

  public abstract String url();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("key")
    Builder key(final String key);

    @JsonProperty("type")
    Builder type(final String type);

    @JsonProperty("description")
    Builder description(final String description);

    @JsonProperty("url")
    Builder url(final String url);

    Repository build();
  }
}
