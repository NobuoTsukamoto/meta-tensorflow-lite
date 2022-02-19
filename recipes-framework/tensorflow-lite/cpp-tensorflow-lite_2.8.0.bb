DESCRIPTION = "TensorFlow Lite CPP "
LICENSE = "Apache-2.0"

LIC_FILES_CHKSUM = "file://LICENSE;md5=4158a261ca7f2525513e31ba9c50ae98"
# Compute branch info from ${PV} as Base PV...
BPV = "${@'.'.join(d.getVar('PV').split('.')[0:2])}"
DPV = "${@'.'.join(d.getVar('PV').split('.')[0:3])}"
# Since they tag off of something resembling ${PV}, use it.
SRCREV = "v${PV}"

SRC_URI = " \
    git://github.com/tensorflow/tensorflow.git;branch=r${BPV};protocol=https \
    file://001-v2.8-Disable-XNNPACKPack-CMakeFile.patch \
    file://001-v2.8_riscv_download.patch \
"

inherit cmake

S = "${WORKDIR}/git"

OECMAKE_SOURCEPATH = "${S}/tensorflow/lite"
EXTRA_OECMAKE = "-DBUILD_SHARED_LIBS=ON"

# Note:
# XNNPack is valid only on 64bit. 
# In the case of arm 32bit, it will be turned off because the build will be
# an error depending on the combination of target CPUs.
HOST_ARCH:raspberrypi = "armv6"
HOST_ARCH:raspberrypi0 = "armv6"
HOST_ARCH:raspberrypi0-wifi = "armv6"
HOST_ARCH:raspberrypi-cm = "armv6"

HOST_ARCH:raspberrypi2 = "armv7"
HOST_ARCH:raspberrypi3 = "armv7"
HOST_ARCH:raspberrypi4 = "armv7"
HOST_ARCH:raspberrypi-cm3 = "armv7"

HOST_ARCH:raspberrypi0-2w-64 = "aarch64"
TUNE_CCARGS:raspberrypi0-2w-64  = ""
EXTRA_OECMAKE:append:raspberrypi0-2w-64 = "-DTFLITE_ENABLE_XNNPACK=ON"
HOST_ARCH:raspberrypi3-64 = "aarch64"
TUNE_CCARGS:raspberrypi3-64 = ""
EXTRA_OECMAKE:append:raspberrypi3-64 = "-DTFLITE_ENABLE_XNNPACK=ON"
HOST_ARCH:raspberrypi4-64 = "aarch64"
TUNE_CCARGS:raspberrypi4-64 = ""
EXTRA_OECMAKE:append:raspberrypi4-64 = "-DTFLITE_ENABLE_XNNPACK=ON"

do_install:append() {
    install -d ${D}/${libdir}
    install -m 0755 ${B}/libtensorflow-lite.so  ${D}/${libdir}/

    install -m 0755 ${B}/_deps/farmhash-build/libfarmhash.so ${D}/${libdir}
    install -m 0755 ${B}/_deps/fft2d-build/libfft2d_fftsg2d.so ${D}/${libdir}
    install -m 0755 ${B}/_deps/fft2d-build/libfft2d_fftsg.so ${D}/${libdir}
    if [ -e ${B}/_deps/ruy-build/libruy.so ]; then
        install -m 0755 ${B}/_deps/ruy-build/libruy.so ${D}/${libdir}
    fi
    if [ -e ${B}/_deps/xnnpack-build/libXNNPACK.so ]; then
        install -m 0755 ${B}/_deps/xnnpack-build/libXNNPACK.so ${D}/${libdir}
    fi
    if [ -e ${B}/pthreadpool/libpthreadpool.so ]; then
        install -m 0755 ${B}/pthreadpool/libpthreadpool.so ${D}/${libdir}
    fi
    if [ -e ${B}/_deps/cpuinfo-build/libcpuinfo.so ]; then
        install -m 0755 ${B}/_deps/cpuinfo-build/libcpuinfo.so ${D}/${libdir}
    fi

    install -d ${D}${includedir}/tensorflow/core/util/
    install -d ${D}${includedir}/tensorflow/lite
    install -d ${D}${includedir}/tensorflow/lite/c
    install -d ${D}${includedir}/tensorflow/lite/core/api
    install -d ${D}${includedir}/tensorflow/lite/delegates
    install -d ${D}${includedir}/tensorflow/lite/delegates/nnapi
    install -d ${D}${includedir}/tensorflow/lite/delegates/xnnpack
    install -d ${D}${includedir}/tensorflow/lite/experimental/resource
    install -d ${D}${includedir}/tensorflow/lite/internal
    install -d ${D}${includedir}/tensorflow/lite/kernels
    install -d ${D}${includedir}/tensorflow/lite/nnapi
    install -d ${D}${includedir}/tensorflow/lite/profiling
    install -d ${D}${includedir}/tensorflow/lite/schema
    install -d ${D}${includedir}/tensorflow/lite/tools
    install -d ${D}${includedir}/tensorflow/lite/tools/evaluation
    install -d ${D}${includedir}/tensorflow/lite/tools/delegates

    install -m 644 ${S}/tensorflow/core/util/*.h ${D}${includedir}/tensorflow/core/util/
    install -m 644 ${S}/tensorflow/lite/*.h ${D}${includedir}/tensorflow/lite
    install -m 644 ${S}/tensorflow/lite/c/*.h ${D}${includedir}/tensorflow/lite/c
    install -m 644 ${S}/tensorflow/lite/core/*.h ${D}${includedir}/tensorflow/lite/core
    install -m 644 ${S}/tensorflow/lite/core/api/*.h ${D}${includedir}/tensorflow/lite/core/api
    install -m 644 ${S}/tensorflow/lite/delegates/*.h ${D}${includedir}/tensorflow/lite/delegates/
    install -m 644 ${S}/tensorflow/lite/delegates/nnapi/*.h ${D}${includedir}/tensorflow/lite/delegates/nnapi
    install -m 644 ${S}/tensorflow/lite/delegates/xnnpack/*.h ${D}${includedir}/tensorflow/lite/delegates/xnnpack
    install -m 644 ${S}/tensorflow/lite/experimental/resource/*.h ${D}${includedir}/tensorflow/lite/experimental/resource
    install -m 644 ${S}/tensorflow/lite/internal/*.h ${D}${includedir}/tensorflow/lite/internal
    install -m 644 ${S}/tensorflow/lite/kernels/*.h ${D}${includedir}/tensorflow/lite/kernels
    install -m 644 ${S}/tensorflow/lite/nnapi/*.h ${D}${includedir}/tensorflow/lite/nnapi
    install -m 644 ${S}/tensorflow/lite/profiling/*.h ${D}${includedir}/tensorflow/lite/profiling
    install -m 644 ${S}/tensorflow/lite/schema/*.h ${D}${includedir}/tensorflow/lite/schema
    install -m 644 ${S}/tensorflow/lite/tools/*.h ${D}${includedir}/tensorflow/lite/tools
    install -m 644 ${S}/tensorflow/lite/tools/evaluation/*.h ${D}${includedir}/tensorflow/lite/tools/evaluation
    install -m 644 ${S}/tensorflow/lite/tools/delegates/*.h ${D}${includedir}/tensorflow/lite/tools/delegates

    install -d ${D}${includedir}/flatbuffers
    install -m 644 ${B}/flatbuffers/include/flatbuffers/*.h ${D}${includedir}/flatbuffers

    install -d ${D}${includedir}/absl
    install -d ${D}${includedir}/absl/algorithm
    install -d ${D}${includedir}/absl/base
    install -d ${D}${includedir}/absl/base/internal
    install -d ${D}${includedir}/absl/cleanup
    install -d ${D}${includedir}/absl/cleanup/internal
    install -d ${D}${includedir}/absl/container
    install -d ${D}${includedir}/absl/container/internal
    install -d ${D}${includedir}/absl/debugging
    install -d ${D}${includedir}/absl/debugging/internal
    install -d ${D}${includedir}/absl/flags
    install -d ${D}${includedir}/absl/flags/internal
    install -d ${D}${includedir}/absl/functional
    install -d ${D}${includedir}/absl/functional/internal
    install -d ${D}${includedir}/absl/hash
    install -d ${D}${includedir}/absl/hash/internal
    install -d ${D}${includedir}/absl/memory
    install -d ${D}${includedir}/absl/meta
    install -d ${D}${includedir}/absl/numeric
    install -d ${D}${includedir}/absl/numeric/internal
    install -d ${D}${includedir}/absl/random
    install -d ${D}${includedir}/absl/random/internal
    install -d ${D}${includedir}/absl/status
    install -d ${D}${includedir}/absl/status/internal
    install -d ${D}${includedir}/absl/strings
    install -d ${D}${includedir}/absl/strings/internal
    install -d ${D}${includedir}/absl/synchronization
    install -d ${D}${includedir}/absl/synchronization/internal
    install -d ${D}${includedir}/absl/time
    install -d ${D}${includedir}/absl/time/internal
    install -d ${D}${includedir}/absl/types
    install -d ${D}${includedir}/absl/types/internal
    install -d ${D}${includedir}/absl/utility

    install -m 644 ${B}/abseil-cpp/absl/algorithm/*.h ${D}${includedir}/absl/algorithm
    install -m 644 ${B}/abseil-cpp/absl/base/*.h ${D}${includedir}/absl/base
    install -m 644 ${B}/abseil-cpp/absl/base/internal/*.h ${D}${includedir}/absl/base/internal
    install -m 644 ${B}/abseil-cpp/absl/cleanup/*.h ${D}${includedir}/absl/cleanup
    install -m 644 ${B}/abseil-cpp/absl/cleanup/internal/*.h ${D}${includedir}/absl/cleanup/internal
    install -m 644 ${B}/abseil-cpp/absl/container/*.h ${D}${includedir}/absl/container
    install -m 644 ${B}/abseil-cpp/absl/container/internal/*.h ${D}${includedir}/absl/container/internal
    install -m 644 ${B}/abseil-cpp/absl/debugging/*.h ${D}${includedir}/absl/debugging
    install -m 644 ${B}/abseil-cpp/absl/debugging/internal/*.h ${D}${includedir}/absl/debugging/internal
    install -m 644 ${B}/abseil-cpp/absl/flags/*.h ${D}${includedir}/absl/flags
    install -m 644 ${B}/abseil-cpp/absl/flags/internal/*.h ${D}${includedir}/absl/flags/internal
    install -m 644 ${B}/abseil-cpp/absl/functional/*.h ${D}${includedir}/absl/functional
    install -m 644 ${B}/abseil-cpp/absl/functional/internal/*.h ${D}${includedir}/absl/functional/internal
    install -m 644 ${B}/abseil-cpp/absl/hash/*.h ${D}${includedir}/absl/hash
    install -m 644 ${B}/abseil-cpp/absl/hash/internal/*.h ${D}${includedir}/absl/hash/internal
    install -m 644 ${B}/abseil-cpp/absl/memory/*.h ${D}${includedir}/absl/memory
    install -m 644 ${B}/abseil-cpp/absl/meta/*.h ${D}${includedir}/absl/meta
    install -m 644 ${B}/abseil-cpp/absl/numeric/internal/*.h ${D}${includedir}/absl/numeric/internal
    install -m 644 ${B}/abseil-cpp/absl/random/*.h ${D}${includedir}/absl/random
    install -m 644 ${B}/abseil-cpp/absl/random/internal/*.h ${D}${includedir}/absl/random/internal
    install -m 644 ${B}/abseil-cpp/absl/status/*.h ${D}${includedir}/absl/status
    install -m 644 ${B}/abseil-cpp/absl/status/internal/*.h ${D}${includedir}/absl/status/internal
    install -m 644 ${B}/abseil-cpp/absl/strings/*.h ${D}${includedir}/absl/strings
    install -m 644 ${B}/abseil-cpp/absl/strings/internal/*.h ${D}${includedir}/absl/strings/internal
    install -m 644 ${B}/abseil-cpp/absl/synchronization/*.h ${D}${includedir}/absl/synchronization
    install -m 644 ${B}/abseil-cpp/absl/synchronization/internal/*.h ${D}${includedir}/absl/synchronization/internal
    install -m 644 ${B}/abseil-cpp/absl/time/*.h ${D}${includedir}/absl/time
    install -m 644 ${B}/abseil-cpp/absl/time/internal/*.h ${D}${includedir}/absl/time/internal
    install -m 644 ${B}/abseil-cpp/absl/types/*.h ${D}${includedir}/absl/types
    install -m 644 ${B}/abseil-cpp/absl/types/internal/*.h ${D}${includedir}/absl/types/internal
    install -m 644 ${B}/abseil-cpp/absl/utility/*.h ${D}${includedir}/absl/utility
}

FILES:${PN}-dev = "${includedir} ${libdir}/libtensorflowlite.so "
FILES:${PN} += "${libdir}/*.so"
