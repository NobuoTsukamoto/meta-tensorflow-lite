DESCRIPTION = "TensorFlow Lite C"
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
TF_MAJOR = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[0]}"
TF_MINOR = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[1]}"
TF_PATCH = "${@(d.getVar('PV').split('.') + ['0', '0', '0'])[2]}"

SRCREV_tensorflow = "a481b10260dfdf833a1b16007eead49c1d7febf3"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;name=tensorflow;branch=r${BPV};protocol=https \
    file://001-Set-CMAKE-SYSTEM-PROCESSOR.patch \
    file://001-flatbuffers.cmake.patch \
"

SRC_URI:append:riscv32 = " \
    file://001-RISCV32_pthreads.patch \
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

TF_CXX_FLAGS = "-DTF_MAJOR_VERSION=${TF_MAJOR} -DTF_MINOR_VERSION=${TF_MINOR} -DTF_PATCH_VERSION=${TF_PATCH} -DTF_VERSION_SUFFIX=''"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite/c"
EXTRA_OECMAKE:append = " \
    -DTFLITE_ENABLE_XNNPACK=OFF \
    -DCMAKE_C_FLAGS='${CFLAGS} ${TF_CXX_FLAGS}' \
    -DCMAKE_CXX_FLAGS='${CXXFLAGS} ${TF_CXX_FLAGS}' \
    -DTENSORFLOW_SOURCE_DIR=${S} \
"

require tensorflow-lite-arch.inc

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
