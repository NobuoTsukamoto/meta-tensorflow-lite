DESCRIPTION = "TensorFlow Lite C"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "6550e4bd80223cdb8be6c3afd1f81e86a4d433c3"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-Set-CMAKE-SYSTEM-PROCESSOR.patch \
    file://001-Fix-neon-sse-file-name-filter.patch \
    file://001-protobuf.cmake.patch \
    file://001-Add-Wno-incompatible-pointer-types-flag-to-xnnpack.cmake.patch \
"

SRC_URI:append:riscv32 = " \
    file://001-RISCV32_pthreads.patch \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
"

SRC_URI:append:riscv64 = " \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
"

inherit cmake

S = "${WORKDIR}/git"

DEPENDS = " \
    libeigen \
    abseil-cpp \
    protobuf-native \
    flatbuffers-native \
"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite/c"
EXTRA_OECMAKE = " \
  -DTFLITE_ENABLE_XNNPACK=OFF \
"

# Note:
# XNNPack is valid only on aarch64 and RISC-V .
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
EXTRA_OECMAKE:append:arm = " -DTFLITE_ENABLE_RUY=ON"
EXTRA_OECMAKE:append:aarch64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:riscv32 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:riscv64 = " -DTFLITE_ENABLE_XNNPACK=ON"

TENSORFLOW_TARGET_ARCH:raspberrypi = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi0 = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi0-wifi = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi-cm = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi2 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi3 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi4 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi-cm3 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi0-2w-64 = "aarch64"
TENSORFLOW_TARGET_ARCH:raspberrypi3-64 = "aarch64"
TENSORFLOW_TARGET_ARCH:raspberrypi4-64 = "aarch64"
TENSORFLOW_TARGET_ARCH:raspberrypi5 = "aarch64"
TENSORFLOW_TARGET_ARCH:riscv32 = "riscv32"
TENSORFLOW_TARGET_ARCH:riscv64 = "riscv64"

# Note:
# Download the submodule using FetchContent_Populate.
# Therefore, turn off FETCHCONTENT_FULLY_DISCONNECTED.
EXTRA_OECMAKE:append = " \
  -DFETCHCONTENT_FULLY_DISCONNECTED=OFF \
  -DTENSORFLOW_TARGET_ARCH=${TENSORFLOW_TARGET_ARCH} \
  -DTFLITE_HOST_TOOLS_DIR=${WORKDIR}/recipe-sysroot-native/usr/bin/ \
 "

do_configure[network] = "1"

do_configure:prepend() {
    rm -rf ${S}/tensorflow/lite/tools/cmake/modules/Findabsl.cmake
    rm -rf ${S}/tensorflow/lite/tools/cmake/modules/FindEigen3.cmake
}

do_configure:append() {
    if [ -e ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch ]; then
        cd ${B}/pthreadpool-source/src
        patch < ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch
    fi
}

do_install() {
    install -d ${D}/${libdir}
    install -m 0755 ${B}/libtensorflowlite_c.so ${D}/${libdir}/

    install -d ${D}${includedir}/tensorflow/lite/c
    install -m 644 ${S}/tensorflow/lite/c/c_api.h ${D}${includedir}/tensorflow/lite/c/
    install -m 644 ${S}/tensorflow/lite/c/common.h ${D}${includedir}/tensorflow/lite/c/
    install -m 644 ${S}/tensorflow/lite/c/c_api_experimental.h ${D}${includedir}/tensorflow/lite/c/
}

FILES:${PN}-dev = "${includedir}"
FILES:${PN} += "${libdir}/libtensorflowlite_c.so"
