DESCRIPTION = "Build and test software of any size, quickly and reliably."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "ce5bec9a6f088da79104fa2cff39cc93"
SRC_URI[sha256sum] = "63873623917c756d1be49ff4d5fc23049736180e6b9a7d5236c6f204eddae3cc"

SRC_URI = "https://github.com/bazelbuild/bazel/releases/download/${PV}/bazel-${PV}-dist.zip"


DEPENDS = "coreutils-native \
           zip-native \
           openjdk-11-native \
           python-native \
           "

S="${WORKDIR}"

do_compile () {
  export JAVA_HOME="${STAGING_LIBDIR_NATIVE}/jvm/openjdk-11-native"
  ./compile.sh
}

do_install () {
  install -d ${D}${bindir}
  cp ${S}/output/bazel ${D}${bindir}
}

# Explicitly disable uninative
UNINATIVE_LOADER = ""
