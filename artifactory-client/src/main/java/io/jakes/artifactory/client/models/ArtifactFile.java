package io.jakes.artifactory.client.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.time.ZonedDateTime;
import java.util.Optional;

@AutoValue
@JsonDeserialize(builder = AutoValue_ArtifactFile.Builder.class)
public abstract class ArtifactFile {
  public abstract String uri();

  public abstract Optional<String> downloadUri();

  public abstract Optional<String> repo();

  public abstract Optional<String> path();

  public abstract Optional<String> remoteUrl();

  public abstract Optional<ZonedDateTime> created();

  public abstract Optional<String> createdBy();

  public abstract ZonedDateTime lastModified();

  public abstract Optional<String> modifiedBy();

  public abstract Optional<ZonedDateTime> lastUpdated();

  public abstract long size();

  public abstract Optional<String> mimeType();

  public abstract boolean folder();

  @AutoValue.Builder
  public interface Builder {
    @JsonProperty("uri")
    Builder uri(final String value);

    @JsonProperty("downloadUri")
    Builder downloadUri(final String value);

    @JsonProperty("repo")
    Builder repo(final String value);

    @JsonProperty("path")
    Builder path(final String value);

    @JsonProperty("remoteUrl")
    Builder remoteUrl(final String value);

    @JsonProperty("created")
    Builder created(final ZonedDateTime value);

    @JsonProperty("lastModified")
    Builder lastModified(final ZonedDateTime value);

    @JsonProperty("lastUpdated")
    Builder lastUpdated(final ZonedDateTime value);

    @JsonProperty("createdBy")
    Builder createdBy(final String value);

    @JsonProperty("modifiedBy")
    Builder modifiedBy(final String value);

    @JsonProperty("size")
    Builder size(final long value);

    @JsonProperty("mimeType")
    Builder mimeType(final String value);

    @JsonProperty("folder")
    Builder folder(final boolean value);

    ArtifactFile build();
  }
}
