plugins {
  java
}

group = "io.jakes.artifactory"
version = "0.1"

repositories {
  mavenCentral()
}

dependencies {
  compile("javax.annotation:javax.annotation-api:1.3.2")

  compile("com.squareup.retrofit2:retrofit:2.6.1")
  implementation("com.squareup.retrofit2:converter-jackson:2.6.1")
  implementation("com.google.guava:guava:28.0-jre")

  compile("com.fasterxml.jackson.core:jackson-databind:2.9.9.3")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:2.9.9")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.9")

  implementation("com.google.auto.value:auto-value-annotations:1.6.6")

  annotationProcessor("com.google.auto.value:auto-value:1.6.6")
}

configure<JavaPluginConvention> {
  sourceCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {

}
