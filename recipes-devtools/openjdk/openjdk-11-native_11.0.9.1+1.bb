DESCRIPTION = "OpenJDK 8 "vanilla" Linux builds done on Travis CI""
LICENSE = "GPL-2.0-with-classpath-exception"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6133e6794362eff6641708cfcc075b80"

SRC_URI[md5sum] = "54908fec126a5bbd257ec652f9d6ed90"
SRC_URI[sha256sum] = "719fa4be55d8036632fcb72999c360ff03f394f351ea5a829efa6729551e4264"
SRC_URI = "https://github.com/ojdkbuild/contrib_jdk11u-ci/releases/download/jdk-11.0.9.1%2B1/jdk-11.0.9-ojdkbuild-linux-x64.zip"

inherit native

INHIBIT_SYSROOT_STRIP = "1"

S = "${WORKDIR}/jdk-11.0.9-ojdkbuild-linux-x64"

do_install () {
  install -d ${D}${libdir}/jvm/openjdk-11-native
  cp -rf ${S}/* ${D}${libdir}/jvm/openjdk-11-native
}


