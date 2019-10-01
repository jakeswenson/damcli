package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

import java.time.ZonedDateTime;

@AutoValue
@JsonDeserialize(builder = AutoValue_ArtifactFolder.Builder.class)
public abstract class ArtifactFolder {
  public abstract String uri();

  public abstract ZonedDateTime created();

  public abstract ImmutableList<ArtifactFile> files();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("uri")
    Builder uri(final String value);

    @JsonProperty("created")
    Builder created(final ZonedDateTime value);

    @JsonProperty("files")
    Builder files(final ImmutableList<ArtifactFile> value);

    ArtifactFolder build();
  }
}
