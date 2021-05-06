DESCRIPTION = "Build and test software of any size, quickly and reliably."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI[md5sum] = "a29c050941876ce693c11ee17f763dec"
SRC_URI[sha256sum] = "70dc0bee198a4c3d332925a32d464d9036a831977501f66d4996854ad4e4fc0d"

SRC_URI = "https://github.com/bazelbuild/bazel/releases/download/${PV}/bazel-${PV}-linux-x86_64"

DEPENDS = "coreutils-native \
           zip-native \
           openjdk-11-native \
           python3-native \
           "

inherit native

INHIBIT_SYSROOT_STRIP = "1"

S="${WORKDIR}"

do_install () {
  install -d ${D}${bindir}
  cp -rf ${S}/bazel-${PV}-linux-x86_64 ${D}${bindir}/bazel
  chmod +x ${D}${bindir}/bazel
}

# Explicitly disable uninative
UNINATIVE_LOADER = ""
