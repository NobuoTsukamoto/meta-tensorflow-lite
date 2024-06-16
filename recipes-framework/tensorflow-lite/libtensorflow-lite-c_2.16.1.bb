DESCRIPTION = "TensorFlow Lite C"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"

SRCREV_tensorflow = "5bc9d26649cca274750ad3625bd93422617eed4b"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-Fix-neon-sse-file-name-filter.patch \
"

SRC_URI:append:riscv32 = " \
    file://001-RISCV32_pthreads.patch \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
    file://001-Fix-RISCV-cpuinfo.patch \
"

SRC_URI:append:riscv64 = " \
    file://001-Disable-XNNPACK-RISC-V-Vector-micro-kernels.patch \
    file://001-Fix-RISCV-cpuinfo.patch \
"

inherit cmake

S = "${WORKDIR}/git"

DEPENDS = " \
    libgfortran \
    libeigen \
    abseil-cpp \
"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite/c"

# Note:
# XNNPack is valid only on 64bit. 
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
EXTRA_OECMAKE:append:raspberrypi0 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi0-wifi = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi0-2w-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:raspberrypi-cm = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi-cm3 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi2 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi3 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi3-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:raspberrypi4 = " -DTFLITE_ENABLE_XNNPACK=OFF"
EXTRA_OECMAKE:append:raspberrypi4-64 = " -DTFLITE_ENABLE_XNNPACK=ON"
EXTRA_OECMAKE:append:raspberrypi5 = " -DTFLITE_ENABLE_XNNPACK=ON"

# Note:
# Download the submodule using FetchContent_Populate.
# Therefore, turn off FETCHCONTENT_FULLY_DISCONNECTED.
EXTRA_OECMAKE:append = " \
  -DFETCHCONTENT_FULLY_DISCONNECTED=OFF\
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

do_install:append() {
    install -d ${D}/${libdir}
    install -m 0755 ${B}/libtensorflowlite_c.so ${D}/${libdir}/

    install -d ${D}${includedir}/tensorflow/lite/c
    install -m 644 ${S}/tensorflow/lite/c/c_api.h ${D}${includedir}/tensorflow/lite/c/
    install -m 644 ${S}/tensorflow/lite/c/common.h ${D}${includedir}/tensorflow/lite/c/
    install -m 644 ${S}/tensorflow/lite/c/c_api_experimental.h ${D}${includedir}/tensorflow/lite/c/
}

FILES:${PN}-dev = "${includedir}"
FILES:${PN} += "${libdir}/libtensorflowlite_c.so"
