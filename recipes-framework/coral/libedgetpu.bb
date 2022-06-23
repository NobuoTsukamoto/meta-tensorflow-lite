DESCRIPTION = "Edge TPU runtime library (libedgetpu)"
HOMEPAGE = "https://github.com/google-coral/libedgetpu"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
PV = "grouper"

SRCREV_libedgetpu = "3164995622300286ef2bb14d7fdc2792dae045b7"
SRCREV_tensorflow = "8a20d54a3c1bfa38c03ea99a2ad3c1b0a45dfa95"

SRC_URI = " \
    git://github.com/google-coral/libedgetpu.git;name=libedgetpu;branch="master";protocol=https \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;destsuffix=tensorflow;branch=r2.9;protocol=https \
    file://001-v2.9_libedgetpu_makefile.patch \
    file://001-v2.9_libedgetpu_allocated_buffer_header.patch \
"

S = "${WORKDIR}/git"
B = "${WORKDIR}/git/makefile_build"

DEPENDS = " \
    flatbuffers-native \
    vim-native \
    flatbuffers \
    abseil-cpp \
    libusb1 \
"

do_configure:prepend () {
    export TFROOT=${WORKDIR}/tensorflow
}

do_compile:prepend() {
    export TFROOT=${WORKDIR}/tensorflow
}

do_install:prepend() {
    install -d ${D}/${libdir}
    install -m 0755 ${S}/out/direct/k8/libedgetpu.so.1 ${D}/${libdir}

    install -d ${D}/etc/udev/rules.d/
    install -m 644 ${S}/debian/edgetpu-accelerator.rules ${D}/etc/udev/rules.d/99-edgetpu-accelerator.rules
}

BBCLASSEXTEND = "native nativesdk"
