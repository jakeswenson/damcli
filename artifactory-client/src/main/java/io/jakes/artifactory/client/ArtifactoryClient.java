package io.jakes.artifactory.client;

import com.google.common.collect.ImmutableList;
import io.jakes.artifactory.client.models.*;
import okhttp3.*;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;

public interface ArtifactoryClient {
  static ArtifactoryClient createClient(final String baseUri, final String key) {
    final OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
          @Override
          public Response intercept(final Chain chain) throws IOException {
            final Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-JFrog-Art-Api", key);
            return chain.proceed(builder.build());
          }
        }).build();

    Retrofit retrofit =
        new Retrofit.Builder()
            .baseUrl(baseUri)
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(Mappers.json()))
            .build();

    ArtifactoryClient service = retrofit.create(ArtifactoryClient.class);

    return service;
  }

  @GET("api/repositories?type=local")
  Call<ImmutableList<Repository>> listRepositories();

  @GET("api/docker/{repoKey}/v2/{dockerRepo}/tags/list")
  Call<DockerTags> listDockerTags(
      @Path("repoKey") final String repoKey,
      @Path("dockerRepo") final String dockerRepo);

  @GET("api/docker/{repoKey}/v2/_catalog")
  Call<DockerRepositories> listDockerRepos(@Path("repoKey") final String repoKey);

  @GET("api/docker/{repoKey}/v2/{dockerRepo}/manifests/{tag}")
  Call<DockerManifest> getDockerManifest(
      @Path("repoKey") final String repoKey,
      @Path("dockerRepo") final String dockerRepo,
      @Path("tag") final String tag);

  @GET("api/storage/{repoKey}/{filePath}")
  Call<ArtifactFile> getFile(
      @Path("repoKey") final String repoKey,
      @Path(value = "filePath", encoded = true) final String filePath);

  @GET("api/storage/{repoKey}/?list&deep=1")
  Call<ArtifactFolder> listFiles(
      @Path("repoKey") final String repoKey);

  @GET("api/storage/{repoKey}/{folderPath}?list&deep=1")
  Call<ArtifactFolder> listFiles(
      @Path("repoKey") final String repoKey,
      @Path(value = "folderPath", encoded = true) final String folderPath);


  @DELETE("api/docker/{repoKey}/v2/{dockerRepo}/manifests/{digest}")
  Call<ResponseBody> deleteDockerByDigest(
      @Path("repoKey") final String repoKey,
      @Path("dockerRepo") final String dockerRepo,
      @Path("digest") final String digest);

}
