workspace(name = "advent2024")


load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

############################
#         Go Lang          #
############################

# Some later deps depend on this.

http_archive(
    name = "io_bazel_rules_go",
    integrity = "sha256-M6zErg9wUC20uJPJ/B3Xqb+ZjCPn/yxFF3QdQEmpdvg=",
    urls = [
        "https://mirror.bazel.build/github.com/bazelbuild/rules_go/releases/download/v0.48.0/rules_go-v0.48.0.zip",
        "https://github.com/bazelbuild/rules_go/releases/download/v0.48.0/rules_go-v0.48.0.zip",
    ],
)

load("@io_bazel_rules_go//go:deps.bzl", "go_register_toolchains", "go_rules_dependencies")

go_rules_dependencies()

go_register_toolchains(version = "1.22.4")

############################
#           gRPC           #
############################

# Rules for Protos
http_archive(
    name = "rules_proto",
    sha256 = "dc3fb206a2cb3441b485eb1e423165b231235a1ea9b031b4433cf7bc1fa460dd",
    strip_prefix = "rules_proto-5.3.0-21.7",
    urls = [
        "https://github.com/bazelbuild/rules_proto/archive/refs/tags/5.3.0-21.7.tar.gz",
    ],
)

load("@rules_proto//proto:repositories.bzl", "rules_proto_dependencies", "rules_proto_toolchains")

rules_proto_dependencies()

rules_proto_toolchains()

# Rules for gRPC C++ and Python.
http_archive(
    name = "com_github_grpc_grpc",
    sha256 = "3115690aafaf139195f4468ef20381b5ae4dc827dd1b8ff9c38367f1d82558e7",
    strip_prefix = "grpc-1.66.1",
    urls = ["https://github.com/grpc/grpc/archive/refs/tags/v1.66.1.zip"],
)

load("@com_github_grpc_grpc//bazel:grpc_deps.bzl", "grpc_deps", "grpc_test_only_deps")

grpc_deps()

grpc_test_only_deps()

load("@com_github_grpc_grpc//bazel:grpc_python_deps.bzl", "grpc_python_deps")

grpc_python_deps()

# Rules for gRPC Java.
http_archive(
    name = "grpc-java",
    sha256 = "28a10159fb36939bf09ce158ac0a5dbadf52ce2a3a47f5c1356b5fdbb9423373",
    strip_prefix = "grpc-java-1.66.0",
    urls = ["https://github.com/grpc/grpc-java/archive/refs/tags/v1.66.0.zip"],
)

load("@grpc-java//:repositories.bzl", "grpc_java_repositories")

grpc_java_repositories()


############################
#          Maven           #
############################

load("@grpc-java//:repositories.bzl", "IO_GRPC_GRPC_JAVA_ARTIFACTS", "IO_GRPC_GRPC_JAVA_OVERRIDE_TARGETS")
load("@rules_jvm_external//:defs.bzl", "maven_install")
load("@rules_jvm_external//:specs.bzl", "maven")

maven_install(
    artifacts = [
        "com.fasterxml.jackson.core:jackson-core:2.17.2",
        "com.fasterxml.jackson.core:jackson-databind:2.17.2",
        "com.github.pcj:google-options:1.0.0",
        "com.google.api-client:google-api-client:2.7.0",
        "com.google.auto.value:auto-value-annotations:1.11.0",
        "com.google.auto.value:auto-value:1.11.0",
        "com.google.code.findbugs:jsr305:3.0.2",
        "com.google.guava:guava:33.2.1-jre",
        "com.google.inject:guice:7.0.0",
        "com.google.protobuf:protobuf-java-util:4.27.3",
        "com.google.protobuf:protobuf-java:4.27.3",
        "com.google.truth.extensions:truth-java8-extension:1.4.4",
        "com.google.truth.extensions:truth-proto-extension:1.4.4",
        "com.google.truth:truth:1.4.4",
        "jakarta.annotation:jakarta.annotation-api:2.1.1",
        "javax.annotation:javax.annotation-api:1.3.2",
        "org.apache.tomcat:annotations-api:6.0.53",
        "org.apache.tomcat:tomcat-annotations-api:11.0.0-M24",
        "org.bouncycastle:bcpkix-jdk18on:jar:1.73",
        "org.bouncycastle:bcprov-jdk18on:jar:1.73",
        "org.junit.jupiter:junit-jupiter-api:5.11.0",
        "org.junit.jupiter:junit-jupiter-engine:5.11.0",
        "org.junit.jupiter:junit-jupiter-params:5.11.0",
        "org.junit.platform:junit-platform-console:1.11.0",
        "org.mockito:mockito-core:5.12.0",
        "org.mockito:mockito-junit-jupiter:5.12.0",
        "org.slf4j:slf4j-api:2.0.16",
        "org.slf4j:slf4j-simple:2.0.16",
    ] + IO_GRPC_GRPC_JAVA_ARTIFACTS,
    generate_compat_repositories = True,
    repositories = [
        "https://repo1.maven.org/maven2",
        "https://maven.google.com",
        "https://jcenter.bintray.com",
        "https://repo.maven.apache.org/maven2",
    ],
)

load("@maven//:compat.bzl", "compat_repositories")

compat_repositories()
