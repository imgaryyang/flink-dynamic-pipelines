import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//________________________________________________________________________________
// DEPENDENCY VERSIONS
//________________________________________________________________________________
val kotlinVersion: String by rootProject.extra
val kotlinCoroutinesVersion: String by rootProject.extra
val vertxVersion: String by rootProject.extra
val juniperVersion: String by rootProject.extra
val koinVersion: String by rootProject.extra
val jacksonVersion: String by rootProject.extra
val guavaVersion: String by rootProject.extra
val flinkVersion: String by rootProject.extra
val flinkScalaRuntime: String by rootProject.extra

//________________________________________________________________________________
// PLUGINS
//________________________________________________________________________________
plugins {
  java
}

//________________________________________________________________________________
// PROJECT SETTINGS
//________________________________________________________________________________
group = "com.lfmunoz"
version = "1.0.0-SNAPSHOT"
val artifactID = "flink-jobs"

//________________________________________________________________________________
// DEPENDENCIES
//________________________________________________________________________________
dependencies {
  //    implementation project(':flink-shared')
  // JSON
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
  implementation("com.google.guava:guava:$guavaVersion")
  // FLINK
  implementation("org.apache.flink:flink-connector-kafka_${flinkScalaRuntime}:${flinkVersion}")
  implementation("org.apache.flink:flink-streaming-java_${flinkScalaRuntime}:${flinkVersion}")
  implementation("org.apache.flink:flink-connector-rabbitmq_${flinkScalaRuntime}:${flinkVersion}")
  testImplementation("org.apache.flink:flink-test-utils_${flinkScalaRuntime}:${flinkVersion}")
  testImplementation("org.apache.flink:flink-streaming-java_${flinkScalaRuntime}:${flinkVersion}")
  // KOTLIN
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
  implementation(kotlin("stdlib-jdk8", kotlinVersion))
  implementation(kotlin("reflect", kotlinVersion))
  // KAFKA
  implementation("org.apache.kafka:kafka-clients:2.2.1")
  // TEST
  testImplementation("org.assertj:assertj-core:3.10.0")
  testImplementation("org.junit.jupiter:junit-jupiter-api:${juniperVersion}")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${juniperVersion}")
  testImplementation("org.awaitility:awaitility:2.0.0")
  testImplementation("org.awaitility:awaitility-kotlin:4.0.1")
}





tasks {

  // setup the environment, for jenkins
  if (project.hasProperty("jenkins")) {
//    systemProperties = [
//      'jenkins'           : true,
//    'ampq'              : "amqp://guest:guest@rabbitNet:5672",
//    'bootstrapServer'   : "kafkaNet:9092",
//    ]
  }

  withType<Jar> {
    manifest.attributes.apply {
      put("Main-Class", "eco.analytics.bridge.FlinkAppKt")
    }
  }

//  withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "1.8"
//  }

  withType<Test> {
    testLogging {
      events("passed", "skipped", "failed")
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      showExceptions = true
      showCauses = true
      showStackTraces = true
      showStandardStreams = false
      showStandardStreams = true
    }
    useJUnitPlatform()
  }

}

