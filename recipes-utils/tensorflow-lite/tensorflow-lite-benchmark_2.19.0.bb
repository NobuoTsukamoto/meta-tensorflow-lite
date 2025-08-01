DESCRIPTION = "TFLite Model Benchmark Tool with C++ Binary"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "e36baa302922ea3c7131b302c2996bd2051ee5c4"

SRC_URI[model.sha256sum] = "1ccb74dbd9c5f7aea879120614e91617db9534bdfaa53dfea54b7c14162e126b"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-Set-CMAKE-SYSTEM-PROCESSOR.patch \
    file://001-protobuf.cmake.patch \
    file://001-flatbuffers.cmake.patch \
    file://001-Add-Wno-incompatible-pointer-types-flag-to-xnnpack.cmake.patch \
    https://storage.googleapis.com/download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=model \
"

SRC_URI:append:riscv32 = " \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
    file://001-RISCV32_pthreads.patch \
    file://001-Add-link-atomic.patch \
"
 
SRC_URI:append:riscv64 = " \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
"

SRC_URI:append:x86-64 = " \
    file://001-Set-CMAKE_POLICY_VERSION_MINIMUM-NEON2SSE.patch \
"

inherit cmake

DEPENDS = " \
    libeigen \
    abseil-cpp \
    protobuf \
    protobuf-native \
    flatbuffers-native \
"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite"
OECMAKE_TARGET_COMPILE = "benchmark_model"
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
EXTRA_OECMAKE:append:x86-64 = " -DTFLITE_ENABLE_XNNPACK=ON"

TENSORFLOW_TARGET_ARCH = "${TARGET_ARCH}"
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
    install -d ${D}${datadir}/tensorflow/lite/tools/benchmark/
    install -m 755 ${B}/tools/benchmark/benchmark_model ${D}${datadir}/tensorflow/lite/tools/benchmark/benchmark_model
    install -m 644 ${UNPACKDIR}/mobilenet_v1_1.0_224.tflite ${D}${datadir}/tensorflow/lite/tools/benchmark/
}

FILES:${PN} += "${datadir}/tensorflow/lite/tools/benchmark/*"
