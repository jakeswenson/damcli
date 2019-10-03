import kotlinx.coroutines.test.withTestContext
import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
  kotlin("jvm") version "1.3.41"
  id("com.palantir.graal") version "0.4.0"
}

group = "io.jakes.artifactory"
version = "0.1"

tasks.withType<Test> {
  useJUnitPlatform()
}

graal {
  mainClass("CliEntrypointKt")
  outputName("damcli")
//  option("--no-fallback")
  option("--allow-incomplete-classpath")
  option("--report-unsupported-elements-at-runtime")
  option("-H:ReflectionConfigurationFiles=${buildDir}/graal-reflection.gen.json,${projectDir}/reflections-config.graal.json")
  option("-H:DynamicProxyConfigurationFiles=${projectDir}/retrofit-dynamic-proxies.graal.json")
  option("-H:+AddAllCharsets")
  if (!OperatingSystem.current().isMacOsX) {
//    option("--static")
  }
}

repositories {
  mavenCentral()
}

val graalVm by configurations.creating

dependencies {
  implementation(kotlin("stdlib-jdk8"))

  implementation(project(":artifactory-client"))

  implementation("info.picocli:picocli:4.0.3")

  implementation("javax.annotation:javax.annotation-api:1.3.2")

  implementation("com.google.dagger:dagger:2.24")
  implementation("com.google.guava:guava:28.0-jre")

  annotationProcessor("com.google.dagger:dagger-compiler:2.24")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
  graalVm("info.picocli:picocli-codegen:4.0.3")
}

tasks.register<Jar>("cliJar") {
  archiveClassifier.set("cli")

  from(sourceSets.main.get().output)

  dependsOn(configurations.runtimeClasspath)
  from({
    configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
  })
  manifest {
    attributes("Main-Class" to "CliEntrypointKt")
  }
}

tasks.named("assemble").get().dependsOn(tasks.named("cliJar").get())

tasks.register<JavaExec>("reflectionConfig") {
  dependsOn(tasks.jar)
  classpath = files(graalVm + tasks.jar.get().archiveFile.get() + sourceSets.main.get().runtimeClasspath)
  main = "picocli.codegen.aot.graalvm.ReflectionConfigGenerator"
  args = listOf("AllCommands", "-o", "${buildDir}/graal-reflection.gen.json")
}

tasks.named("nativeImage").get().dependsOn(tasks.named("reflectionConfig").get())

tasks.withType<KotlinCompile> {
  val jvmOptions = kotlinOptions.cast<KotlinJvmOptions>()
  jvmOptions.jvmTarget = "1.8"
}
