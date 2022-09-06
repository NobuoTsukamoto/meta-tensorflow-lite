DESCRIPTION = "TFLite Model Benchmark Tool with C++ Binary"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "359c3cdfc5fabac82b3c70b3b6de2b0a8c16874f"

SRC_URI[model.sha256sum] = "1ccb74dbd9c5f7aea879120614e91617db9534bdfaa53dfea54b7c14162e126b"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-v2.10-Fix-CMAKE_Build_Error.patch \
    file://001-v2.10-Disable-XNNPACKPack-CMakeFile.patch \
    file://001-v2.10-Add-CMAKE_SYSTEM_PROCESSOR.patch \
    https://storage.googleapis.com/download.tensorflow.org/models/mobilenet_v1_2018_02_22/mobilenet_v1_1.0_224.tgz;name=model \
"

SRC_URI:append:riscv32 = " \
    file://001-v2.10-RISCV32_pthreads.patch \
"

inherit cmake

S = "${WORKDIR}/git"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite"
OECMAKE_TARGET_COMPILE = "benchmark_model"

# Note:
# XNNPack is valid only on 64bit. 
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
TENSORFLOW_TARGET_ARCH:raspberrypi = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi0 = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi0-wifi = "armv6"
TENSORFLOW_TARGET_ARCH:raspberrypi-cm = "armv6"

TENSORFLOW_TARGET_ARCH:raspberrypi2 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi3 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi4 = "armv7"
TENSORFLOW_TARGET_ARCH:raspberrypi-cm3 = "armv7"

TENSORFLOW_TARGET_ARCH:raspberrypi0-2w-64 = "aarch64"
TUNE_CCARGS:raspberrypi0-2w-64  = ""
EXTRA_OECMAKE:append:raspberrypi0-2w-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
TENSORFLOW_TARGET_ARCH:raspberrypi3-64 = "aarch64"
TUNE_CCARGS:raspberrypi3-64 = ""
EXTRA_OECMAKE:append:raspberrypi3-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
TENSORFLOW_TARGET_ARCH:raspberrypi4-64 = "aarch64"
TUNE_CCARGS:raspberrypi4-64 = ""
EXTRA_OECMAKE:append:raspberrypi4-64 = " -DTFLITE_ENABLE_XNNPACK=ON"

TENSORFLOW_TARGET_ARCH:riscv32 = "riscv32"
EXTRA_OECMAKE:append:riscv32 = " -DTFLITE_ENABLE_XNNPACK=ON"
TENSORFLOW_TARGET_ARCH:riscv64 = "riscv64"
EXTRA_OECMAKE:append:riscv64 = " -DTFLITE_ENABLE_XNNPACK=ON"

# Note:
# Download the submodule using FetchContent_Populate.
# Therefore, turn off FETCHCONTENT_FULLY_DISCONNECTED.
EXTRA_OECMAKE:append = " -DFETCHCONTENT_FULLY_DISCONNECTED=OFF -DTENSORFLOW_TARGET_ARCH=${TENSORFLOW_TARGET_ARCH}"

do_configure[network] = "1"

do_configure:append() {
    if [ -e ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch ]; then
        cd ${B}/pthreadpool-source/src
        patch < ${S}/tensorflow/lite/tools/pip_package/riscv32_pthread.patch
    fi
}

do_install:append() {
    install -d ${D}${datadir}/tensorflow/lite/tools/benchmark/
    install -m 755 ${B}/tools/benchmark/benchmark_model ${D}${datadir}/tensorflow/lite/tools/benchmark/benchmark_model
    install -m 644 ${WORKDIR}/mobilenet_v1_1.0_224.tflite ${D}${datadir}/tensorflow/lite/tools/benchmark/
}

FILES:${PN} += "${datadir}/tensorflow/lite/tools/benchmark/*"
