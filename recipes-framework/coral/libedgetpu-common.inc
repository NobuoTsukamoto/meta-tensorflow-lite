DESCRIPTION = "Edge TPU runtime library (libedgetpu)"
HOMEPAGE = "https://github.com/google-coral/libedgetpu"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
PV = "grouper"
SRCREV_FORMAT = "libedgetpu_tensorflow"

SRCREV_libedgetpu = "e35aed18fea2e2d25d98352e5a5bd357c170bd4d"
SRCREV_tensorflow = "e36baa302922ea3c7131b302c2996bd2051ee5c4"

SRC_URI = " \
    git://github.com/google-coral/libedgetpu.git;name=libedgetpu;branch="master";protocol=https \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;destsuffix=tensorflow;branch=r2.19;protocol=https \
    file://001-libedgetpu_makefile.patch \
    file://001-flatbuffers.cmake.patch;patchdir=${UNPACKDIR}/tensorflow \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/makefile_build"

DEPENDS = " \
    flatbuffers-native \
    flatbuffers \
    vim-native \
    libtensorflow-lite \
    libusb1 \
"

EXTRA_OEMAKE = " libedgetpu"

do_configure:prepend () {
    export TFROOT=${UNPACKDIR}/tensorflow
}

do_compile:prepend() {
    export TFROOT=${UNPACKDIR}/tensorflow
}

BBCLASSEXTEND = "native nativesdk"
INSANE_SKIP:${PN} += "buildpaths"
